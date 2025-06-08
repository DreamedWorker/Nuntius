package icu.bluedream.nuntius.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NTTextMessage(
    @SerialName("group_id") val groupID: String,
    val message: List<Message>
) {
    @Serializable
    data class Message(
        val type: String,
        val data: MsgContext
    ) {
        @Serializable
        data class MsgContext(
            val text: String
        )
    }
}

@Serializable
data class NTPrivateTextMessage(
    @SerialName("user_id") val userID: String,
    val message: List<NTTextMessage.Message>
)