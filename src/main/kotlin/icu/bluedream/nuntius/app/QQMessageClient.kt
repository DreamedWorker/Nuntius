package icu.bluedream.nuntius.app

import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.app.data.NTImageFileMessage
import icu.bluedream.nuntius.app.data.NTPrivateImageFileMessage
import icu.bluedream.nuntius.app.data.NTPrivateTextMessage
import icu.bluedream.nuntius.app.data.NTTextMessage
import icu.bluedream.nuntius.app.data.NapcatResult
import icu.bluedream.nuntius.config
import icu.bluedream.nuntius.logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.json.JSONObject

object QQMessageClient {
    private val client: HttpClient by lazy {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    suspend fun sendImageMessage(imageBase64: String, msgBody: MessageEvent): NapcatResult {
        val message = listOf(
            NTImageFileMessage.ImageFileMessage(
                type = "image",
                data = NTImageFileMessage.ImageFileMessage.LocalFile(
                    file = "base64://${imageBase64}",
                    summary = "[图片]"
                )
            )
        )
        val msg = if (msgBody.groupId != 0L) NTImageFileMessage(groupID = msgBody.groupId.toString(), message)
        else NTPrivateImageFileMessage(userID = msgBody.sender.userId.toString(), message)
        val path = if (msgBody.groupId != 0L) "/send_group_msg" else "/send_private_msg"
        val response = client.post(getHttpURL(path)) {
            contentType(ContentType.Application.Json)
            setBody(msg)
        }
        return response.body<NapcatResult>()
    }

    suspend fun sendTextMessage(text: String, msgBody: MessageEvent): NapcatResult {
        val message = listOf(
            NTTextMessage.Message(
                type = "text",
                data = NTTextMessage.Message.MsgContext(
                    text
                )
            )
        )
        val msg = if (msgBody.groupId != 0L) NTTextMessage(groupID = msgBody.groupId.toString(), message)
        else NTPrivateTextMessage(userID = msgBody.sender.userId.toString(), message)
        val path = if (msgBody.groupId != 0L) "/send_group_msg" else "/send_private_msg"
        val response = client.post(getHttpURL(path)) {
            contentType(ContentType.Application.Json)
            setBody(msg)
        }
        return response.body<NapcatResult>()
    }

    suspend fun deleteMessage(msgId: String?) {
        if (msgId != null) {
            val body = with(JSONObject()) {
                put("message_id", msgId)
                toString()
            }
            val response = client.post(getHttpURL("/delete_msg")) {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            val result = response.body<String>()
            logger.info("撤回消息${msgId}的状态：${result}")
        }
    }

    private fun getHttpURL(method: String): String {
        val networkConfig = config.element("BotNetwork")
        val address = networkConfig.element("BaseUrl").text
        val httpPorts = networkConfig.element("HttpPort")
        val useHttps = httpPorts.attribute("useHttps").text == "true"
        val token = networkConfig.element("AccessToken")
        return if (token.attribute("enabled").text == "true") {
            "${if (useHttps) "https://" else "http://"}${address}:${httpPorts.text}${method}?access_token=${token.text}"
        } else {
            "${if (useHttps) "https://" else "http://"}${address}:${httpPorts.text}${method}"
        }
    }
}