package icu.bluedream.nuntius.genshin.impl.account

import icu.bluedream.nuntius.app.config.DeviceEnv
import icu.bluedream.nuntius.app.db.DatabaseHelper
import icu.bluedream.nuntius.app.extension.file.img2base64
import icu.bluedream.nuntius.app.extension.request.setDS
import icu.bluedream.nuntius.app.extension.request.setUser
import icu.bluedream.nuntius.app.util.LocalPath
import icu.bluedream.nuntius.app.web.buildPostRequest
import icu.bluedream.nuntius.app.web.buildRequest
import icu.bluedream.nuntius.app.web.defaultHoyoClient
import icu.bluedream.nuntius.app.web.getAsStruct
import icu.bluedream.nuntius.app.web.toRequestBody
import icu.bluedream.nuntius.cq.NTMessageSender
import icu.bluedream.nuntius.genshin.ApiEndpoints
import icu.bluedream.nuntius.genshin.impl.account.func.getCookieToken
import icu.bluedream.nuntius.genshin.impl.account.func.getSTokenByGameToken
import icu.bluedream.nuntius.genshin.model.account.GameBasicInfo
import icu.bluedream.nuntius.genshin.model.account.LToken
import icu.bluedream.nuntius.genshin.model.account.QRCodeScanResult
import icu.bluedream.nuntius.genshin.model.account.QRCodeToken
import icu.bluedream.nuntius.genshin.util.DynamicSecret
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
    suspend fun bindingUid(group: String, sender: String) {
        if (canInsert(sender)) {
            val code = generateQRCode()
            val msgQRResult = NTMessageSender.sendImageMessage(code["codeFile"]!!.img2base64(), group)
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
                                qqCode = sender,
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
                                    NTMessageSender.sendTextMessage(
                                        "我们已为「${sender}」绑定了UID：${gameInfo.gameUid}",
                                        group
                                    )
                                }.await()
                                if (msgResult.retcode == 0) {
                                    delay(5.seconds)
                                    NTMessageSender.deleteMessage(msgResult.data?.id.toString())
                                    delay(1.seconds)
                                    NTMessageSender.deleteMessage(msgQRResult.data?.id.toString())
                                    LocalPath.deleteFile(code["codeFile"] as String)
                                }
                            } else {
                                throw LoginException("出现未知问题，我们无法绑定UID。")
                            }
                        }
                    },
                    onFailedOrExpired = { msg ->
                        throw LoginException("我们终止了${sender}的本次登录，${msg}")
                    }
                )
            } else {
                throw LoginException("我们无法发送登录用二维码到群：${group}")
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
            toString().toRequestBody()
        }
        while (retryCount < maxRetries) {
            val result = buildPostRequest(
                { url(ApiEndpoints.getQRQuery()) },
                requestBody
            ).getAsStruct<QRCodeScanResult>(defaultHoyoClient)
            if (result.retcode != 0L) {
                onFailedOrExpired("此二维码已过期")
                break
            }
            if (result.data.stat == "Confirmed") {
                val raw = result.data.payload.raw
                onScanned(Json.decodeFromString<QRCodeToken>(raw))
                break
            }
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
        val result = buildRequest {
            url(ApiEndpoints.getGameBasic())
            setUser(uid, stoken, mid)
            setDS(DynamicSecret.Version.Gen1, DynamicSecret.SaltType.K2)
            addHeader("Host", "api-takumi.miyoushe.com")
            addHeader("Referer", "https://app.mihoyo.com")
            addHeader("Origin", "https://api-takumi.miyoushe.com")
            addHeader("X-Requested-With", "com.mihoyo.hyperion")
        }.getAsStruct<GameBasicInfo>(defaultHoyoClient)
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
        val result = buildRequest {
            url(ApiEndpoints.getLToken())
            setUser(uid, stoken, mid)
        }.getAsStruct<LToken>(defaultHoyoClient)
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
            toString().toRequestBody()
        }
        val result = buildPostRequest(
            {
                url(ApiEndpoints.getQRFetch())
            },
            requestBody
        ).getAsStruct<icu.bluedream.nuntius.genshin.model.account.QRCode>(defaultHoyoClient)
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