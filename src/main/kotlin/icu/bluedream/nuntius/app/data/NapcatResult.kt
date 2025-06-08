package icu.bluedream.nuntius.app.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NapcatResult(
    val status: String,
    val retcode: Int,
    val data: MessageId?,
    val message: String?,
    val wording: String?,
    val echo: String?
) {
    @Serializable
    data class MessageId(
        @SerialName("message_id") val id: Long
    )
}
