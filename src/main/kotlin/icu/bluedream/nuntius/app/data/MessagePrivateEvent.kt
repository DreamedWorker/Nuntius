package icu.bluedream.nuntius.app.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
@JsonIgnoreUnknownKeys
@OptIn(ExperimentalSerializationApi::class)
data class MessagePrivateEvent(
    val time: Long,
    @SerialName("message_id")
    val messageID: Long,
    val sender: Sender,
    val message: List<Message>,
) {
    @Serializable
    @JsonIgnoreUnknownKeys
    data class Sender (
        @SerialName("user_id")
        val userID: Long,
        val nickname: String,
    )

    @Serializable
    data class Message (
        val type: String,
        val data: Data
    ) {
        @Serializable
        data class Data (
            val text: String
        )
    }
}
