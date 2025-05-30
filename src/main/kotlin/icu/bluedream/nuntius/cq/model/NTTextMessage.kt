package icu.bluedream.nuntius.ntqq.struct

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