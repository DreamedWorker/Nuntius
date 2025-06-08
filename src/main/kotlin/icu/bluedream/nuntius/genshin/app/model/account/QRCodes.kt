package icu.bluedream.nuntius.genshin.app.model.account

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@Serializable
data class QRCode(
    val retcode: Int,
    val message: String,
    val data: QRCodeData
) {
    @Serializable
    data class QRCodeData(
        val url: String
    )
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class QRCodeScanResult(
    val retcode: Long,
    val message: String,
    val data: ScanResultData
) {
    @Serializable
    @JsonIgnoreUnknownKeys
    data class ScanResultData(
        val stat: String,
        val payload: Payload
    ) {
        @JsonIgnoreUnknownKeys
        @Serializable
        data class Payload(
            val raw: String,
        )
    }
}

@Serializable
data class QRCodeToken(
    val uid: String,
    val token: String
)