package icu.bluedream.nuntius.app

import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.app.data.MessagePrivateEvent
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.dom4j.Element

object WebsocketClient {
    private val client by lazy {
        HttpClient(CIO) {
            install(WebSockets)
        }
    }

    fun connectToServer(
        webConfig: Element,
        service: suspend (MessageEvent, Boolean) -> Unit
    ) {
        runBlocking {
            client.webSocket(
                urlString = generateAddress(webConfig)
            ) {
                while (true) {
                    val otherWorldsMessage = incoming.receive() as? Frame.Text
                    val text = otherWorldsMessage?.readText()
                    val json = text?.let { Json.Default.parseToJsonElement(it) }
                    if (json?.jsonObject["message"]?.jsonArray[0]?.jsonObject["type"]?.jsonPrimitive?.content == "text") {
                        val msgType = json.jsonObject["message_type"]?.jsonPrimitive?.content
                        when (msgType) {
                            "private" -> {
                                val type = json.jsonObject["post_type"]?.jsonPrimitive?.content
                                when(type) {
                                    "message" -> {
                                        val temp = text
                                            .let { Json.Default.decodeFromString<MessagePrivateEvent>(it) }
                                        val result = MessageEvent(
                                            time = temp.time,
                                            sender = MessageEvent.Sender(
                                                userId = temp.sender.userID,
                                                nickname = temp.sender.nickname
                                            ),
                                            message = listOf(
                                                MessageEvent.Message(
                                                    type = "text",
                                                    data = MessageEvent.Message.Content(
                                                        text = temp.message.first().data.text
                                                    )
                                                )
                                            ),
                                            groupId = 0L
                                        )
                                        service(result, false)
                                    }
                                }
                            }

                            "group" -> {
                                val type = json.jsonObject["post_type"]?.jsonPrimitive?.content
                                when (type) {
                                    "message" -> {
                                        val result = text
                                            .let { Json.Default.decodeFromString<MessageEvent>(it) }
                                        service(result, true)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun generateAddress(config: Element): String {
        val address = config.element("BaseUrl").text
        val port = config.element("WebSocketPort").text
        val token = config.element("AccessToken")
        return if (token.attribute("enabled").text == "true") {
            "ws://${address}:${port}/?access_token=${token.text}"
        } else {
            "ws://${address}:${port}/"
        }
    }
}