package icu.bluedream.nuntius.app.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class MessageEvent(
    val time: Long,
    val sender: Sender,
    val message: List<Message>,
    @SerialName("group_id") val groupId: Long
) {
    @Serializable
    @JsonIgnoreUnknownKeys
    data class Sender(
        @SerialName("user_id") val userId: Long,
        val nickname: String
    )

    @Serializable
    @JsonIgnoreUnknownKeys
    data class Message(
        val type: String,
        val data: Content
    ) {
        @Serializable
        @JsonIgnoreUnknownKeys
        data class Content(
            val text: String
        )
    }
}