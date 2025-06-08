package icu.bluedream.nuntius.genshin.impl.account

import icu.bluedream.nuntius.app.LocalPath
import icu.bluedream.nuntius.app.QQMessageClient
import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.app.extension.file.img2base64
import icu.bluedream.nuntius.genshin.app.database.DatabaseHelper
import icu.bluedream.nuntius.genshin.app.model.account.GameBasicInfo
import icu.bluedream.nuntius.genshin.app.model.account.LToken
import icu.bluedream.nuntius.genshin.app.model.account.QRCodeScanResult
import icu.bluedream.nuntius.genshin.app.model.account.QRCodeToken
import icu.bluedream.nuntius.genshin.app.util.DynamicSecret
import icu.bluedream.nuntius.genshin.app.web.ApiEndpoints
import icu.bluedream.nuntius.genshin.app.web.HoyoWebClient
import icu.bluedream.nuntius.genshin.impl.DeviceEnv
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import net.glxn.qrgen.QRCode
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.json.JSONObject
import java.io.File
import java.net.URI
import java.net.URISyntaxException
import kotlin.time.Duration.Companion.seconds

typealias LoginPreload = MutableMap<String, String>

object BindingUidJob {
    @Throws(LoginException::class)
    suspend fun bindingUid(msgBody: MessageEvent) {
        if (canInsert(msgBody.sender.userId.toString())) {
            val code = generateQRCode()
            val msgQRResult = QQMessageClient.sendImageMessage(code["codeFile"]!!.img2base64(), msgBody)
            if (msgQRResult.retcode == 0) {
                val ticket = parseQueryString(code["codeURL"]!!)["ticket"]
                if (ticket == null) {
                    throw LoginException("我们无法获取登录票据")
                }
                login(
                    ticket,
                    onScanned = { tokens ->
                        runBlocking {
                            val sToken = async { getSTokenByGameToken(tokens) }.await()
                            val ckToken = async { getCookieToken(tokens) }.await()
                            val gameInfo = async {
                                fetchGenshinInfo(
                                    uid = sToken.user.aid,
                                    stoken = sToken.token.token,
                                    mid = sToken.user.mid
                                )
                            }.await()
                            val lToken = async {
                                fetchLToken(
                                    uid = sToken.user.aid,
                                    stoken = sToken.token.token,
                                    mid = sToken.user.mid
                                )
                            }.await()
                            val account = HoyoAccountEntity(
                                qqCode = msgBody.sender.userId.toString(),
                                cookieToken = ckToken.data.cookieToken,
                                gameToken = tokens.token,
                                ltoken = lToken,
                                mid = sToken.user.mid,
                                stoken = sToken.token.token,
                                stuid = tokens.uid,
                                genshinNicname = gameInfo.nickname,
                                genshinPicID = "other",
                                genshinUID = gameInfo.gameUid,
                                level = gameInfo.level.toString(),
                                serverName = gameInfo.regionName,
                                serverRegion = gameInfo.region
                            )
                            if (DatabaseHelper.insertHoyoAccount(account)) {
                                val msgResult = async {
                                    QQMessageClient.sendTextMessage(
                                        "我们已为「${msgBody.sender.userId}」绑定了UID：${gameInfo.gameUid}",
                                        msgBody
                                    )
                                }.await()
                                if (msgResult.retcode == 0) {
                                    delay(5.seconds)
                                    QQMessageClient.deleteMessage(msgResult.data?.id.toString())
                                    delay(1.seconds)
                                    QQMessageClient.deleteMessage(msgQRResult.data?.id.toString())
                                    LocalPath.deleteFile(code["codeFile"] as String)
                                }
                            } else {
                                throw LoginException("出现未知问题，我们无法绑定UID。")
                            }
                        }
                    },
                    onFailedOrExpired = { msg ->
                        throw LoginException("我们终止了${msgBody.sender.userId}的本次登录，${msg}")
                    }
                )
            } else {
                throw LoginException("我们无法发送登录用二维码")
            }
        } else {
            throw LoginException("此QQ号已经绑定过UID了！")
        }
    }

    private suspend fun login(
        ticket: String,
        onScanned: (QRCodeToken) -> Unit,
        onFailedOrExpired: (String) -> Unit,
        maxRetries: Int = 15,
        interval: Long = 3L
    ) {
        var retryCount = 0
        val requestBody = with(JSONObject()) {
            put("app_id", "2")
            put("device", DeviceEnv.deviceId)
            put("ticket", ticket)
            toString()
        }
        while (retryCount < maxRetries) {
            val request = HoyoWebClient.hoyoClient.post(ApiEndpoints.getQRQuery()) {
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }
            val result = request.body<QRCodeScanResult>()
            if (result.retcode != 0L) {
                onFailedOrExpired("此二维码已过期")
                break
            }
            if (result.data.stat == "Confirmed") {
                val raw = result.data.payload.raw
                onScanned(Json.decodeFromString<QRCodeToken>(raw))
                break
            }
//            val result = buildPostRequest(
//                { url(ApiEndpoints.getQRQuery()) },
//                requestBody
//            ).getAsStruct<QRCodeScanResult>(defaultHoyoClient)
//            if (result.retcode != 0L) {
//                onFailedOrExpired("此二维码已过期")
//                break
//            }
//            if (result.data.stat == "Confirmed") {
//                val raw = result.data.payload.raw
//                onScanned(Json.decodeFromString<QRCodeToken>(raw))
//                break
//            }
            retryCount++
            delay(interval.seconds)
        }
    }

    @Throws(LoginException::class)
    private suspend fun fetchGenshinInfo(
        uid: String,
        stoken: String,
        mid: String
    ): GameBasicInfo.GameBasicInfoClass.GameBasic {
        val result = HoyoWebClient.hoyoClient.get(ApiEndpoints.getGameBasic()) {
            header("cookie", "stuid=${uid};stoken=${stoken};mid=${mid}")
            header("DS", DynamicSecret.getDynamicSecret(DynamicSecret.Version.Gen1, DynamicSecret.SaltType.K2))
            header("Host", "api-takumi.miyoushe.com")
            header("Referer", "https://app.mihoyo.com")
            header("Origin", "https://api-takumi.miyoushe.com")
            header("X-Requested-With", "com.mihoyo.hyperion")
        }.body<GameBasicInfo>()
//        val result = buildRequest {
//            url(ApiEndpoints.getGameBasic())
//            setUser(uid, stoken, mid)
//            setDS(DynamicSecret.Version.Gen1, DynamicSecret.SaltType.K2)
//            addHeader("Host", "api-takumi.miyoushe.com")
//            addHeader("Referer", "https://app.mihoyo.com")
//            addHeader("Origin", "https://api-takumi.miyoushe.com")
//            addHeader("X-Requested-With", "com.mihoyo.hyperion")
//        }.getAsStruct<GameBasicInfo>(defaultHoyoClient)
        if (result.retcode != 0L) {
            throw LoginException("我们无法获取你的游戏信息：${result.message}")
        }
        val games = result.data.list.filter { it -> it.gameBiz == "hk4e_cn" }
        if (games.isEmpty()) {
            throw LoginException("UID：${uid}并未绑定原神角色")
        }
        return games.first()
    }

    @Throws(LoginException::class)
    private suspend fun fetchLToken(uid: String, stoken: String, mid: String): String {
        val result = HoyoWebClient.hoyoClient.get(ApiEndpoints.getLToken()) {
            header("cookie", "stuid=${uid};stoken=${stoken};mid=${mid}")
        }.body<LToken>()
//        val result = buildRequest {
//            url(ApiEndpoints.getLToken())
//            setUser(uid, stoken, mid)
//        }.getAsStruct<LToken>(defaultHoyoClient)
        if (result.retcode != 0L) {
            throw LoginException("我们无法获取你的LToken：${result.message}")
        }
        return result.data.ltoken
    }

    @Throws(LoginException::class)
    private suspend fun generateQRCode(): LoginPreload {
        val prepared = mutableMapOf<String, String>()
        val requestBody = with(JSONObject()) {
            put("app_id", "2")
            put("device", DeviceEnv.deviceId)
            toString()
        }
        val result = HoyoWebClient.hoyoClient.post(ApiEndpoints.getQRFetch()) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.body<icu.bluedream.nuntius.genshin.app.model.account.QRCode>()
//        val result = buildPostRequest(
//            {
//                url(ApiEndpoints.getQRFetch())
//            },
//            requestBody
//        ).getAsStruct<icu.bluedream.nuntius.genshin.app.model.account.QRCode>(defaultHoyoClient)
        if (result.retcode != 0) {
            throw LoginException("无法获取登录用二维码：${result.message}")
        }
        prepared.put("codeURL", result.data.url)
        prepared.put("codeFile", dealQRCodeFile(result.data.url))
        return prepared
    }

    private fun dealQRCodeFile(url: String): String {
        val filename = "${System.currentTimeMillis()}_qr.png"
        val requiredPath = LocalPath.getPathFile(
            LocalPath.tempDir,
            filename
        ).absolutePath
        LocalPath.createEmptyFile(requiredPath)
        val qrcodeFile = QRCode.from(url).file(filename)
        qrcodeFile.copyTo(File(requiredPath), true)
        qrcodeFile.delete()
        return requiredPath
    }

    @Throws(URISyntaxException::class)
    private fun parseQueryString(url: String): Map<String, String> {
        val uri = URI(url)
        val query = uri.query
        val queryPairs = HashMap<String, String>()
        val pairs = query.split("&")
        for (pair in pairs) {
            val idx = pair.indexOf('=')
            queryPairs.put(
                if (idx > 0) pair.substring(0, idx) else pair,
                if (idx > 0) pair.substring(idx + 1) else ""
            )
        }
        return queryPairs
    }

    private fun canInsert(qqCode: String): Boolean {
        return !transaction(DatabaseHelper.connection) {
            HoyoAccounts.selectAll()
                .where { HoyoAccounts.qqCode eq qqCode }
                .limit(1)
                .any()
        }
    }
}