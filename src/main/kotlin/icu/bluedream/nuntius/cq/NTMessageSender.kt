package icu.bluedream.nuntius.cq

import icu.bluedream.nuntius.LOGGER
import icu.bluedream.nuntius.app.web.buildPostRequest
import icu.bluedream.nuntius.app.web.getAsString
import icu.bluedream.nuntius.app.web.getAsStruct
import icu.bluedream.nuntius.app.web.toRequestBody
import icu.bluedream.nuntius.configBot
import icu.bluedream.nuntius.ntqq.struct.NTImageFileMessage
import icu.bluedream.nuntius.ntqq.struct.NTTextMessage
import kotlinx.serialization.json.Json
import org.json.JSONObject

object NTMessageSender {
    private val schema = if (configBot["use_https"] as Int == 1) "https" else "http"
    private val access = configBot["access_token"] as String

    suspend fun sendImageMessage(imageBase64: String, groupId: String): NapcatResult {
        val url = "${schema}://${configBot["server_url"]}:${configBot["http_port"]}/send_group_msg${if (access.isNotBlank()) "?access_token=${access}" else ""}"
        val msg = NTImageFileMessage(
            groupID = groupId,
            message = listOf(
                NTImageFileMessage.ImageFileMessage(
                    type = "image",
                    data = NTImageFileMessage.ImageFileMessage.LocalFile(
                        file = "base64://${imageBase64}",
                        summary = "[图片]"
                    )
                )
            )
        )
        val sendResult = buildPostRequest(
            {
                url(url)
                addHeader("Content-Type", "application/json")
            },
            Json.encodeToString(msg).toRequestBody()
        ).getAsStruct<NapcatResult>()
        return sendResult
    }

    suspend fun sendTextMessage(text: String, group: String): NapcatResult {
        val url = "${schema}://${configBot["server_url"]}:${configBot["http_port"]}/send_group_msg${if (access.isNotBlank()) "?access_token=${access}" else ""}"
        val msg = NTTextMessage(
            groupID = group,
            message = listOf(
                NTTextMessage.Message(
                    type = "text",
                    data = NTTextMessage.Message.MsgContext(
                        text
                    )
                )
            )
        )
        val result = buildPostRequest(
            {
                url(url)
                addHeader("Content-Type", "application/json")
            },
            Json.encodeToString(msg).toRequestBody()
        ).getAsStruct<NapcatResult>()
        return result
    }

    suspend fun deleteMessage(msgId: String?) {
        val url = "${schema}://${configBot["server_url"]}:${configBot["http_port"]}/delete_msg${if (access.isNotBlank()) "?access_token=${access}" else ""}"
        if (msgId != null) {
            val body = with(JSONObject()) {
                put("message_id", msgId)
                toString().toRequestBody()
            }
            val result = buildPostRequest(
                {
                    url(url)
                    addHeader("Content-Type", "application/json")
                },
                body
            ).getAsString()
            LOGGER.info("撤回消息${msgId}的状态：${result}")
        }
    }
}