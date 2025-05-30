package icu.bluedream.nuntius.app.web

import icu.bluedream.nuntius.cq.model.MessageEvent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener

object WebsocketHelper {
    private val generalClient = OkHttpClient()

    fun websocketHelper(
        webConfig: Map<String, Any>,
        onMessageReceiver: (MessageEvent) -> Unit
    ) {
        val access = webConfig["access_token"] as String
        val url = "ws://${webConfig["server_url"]}:${webConfig["websocket_port"]}${if (access.isNotBlank()) "/?access_token=${access}" else ""}"
        val ws = Request.Builder()
            .url(url)
            .build()
        generalClient.newWebSocket(ws, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = Json.Default.parseToJsonElement(text)
                val type = json.jsonObject["post_type"]?.jsonPrimitive?.content
                when(type) {
                    "message" -> {
                        val result = getOrNull { Json.Default.decodeFromString<MessageEvent>(text) }
                        if (result != null) {
                            onMessageReceiver(result)
                        }
                    }
                }
            }
        })
    }

    private inline fun <reified T> getOrNull(block: () -> T): T? {
        return try {
            block()
        } catch (_: Exception) {
            null
        }
    }
}