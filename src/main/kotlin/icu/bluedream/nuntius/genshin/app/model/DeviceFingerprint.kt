package icu.bluedream.nuntius.genshin.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DeviceFingerprint(
    val retcode: Int,
    val message: String,
    val data: DataClass
) {
    @Serializable
    data class DataClass(
        @SerialName("device_fp") val deviceFP: String,
        val code: Int,
        val msg: String
    )
}
