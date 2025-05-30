package icu.bluedream.nuntius.ntqq.struct

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NTImageFileMessage(
    @SerialName("group_id") val groupID: String,
    val message: List<ImageFileMessage>
) {
    @Serializable
    data class ImageFileMessage(
        val type: String, // image
        val data: LocalFile
    ) {
        @Serializable
        data class LocalFile(
            val file: String, // file:///path
            val summary: String
        )
    }
}