package icu.bluedream.nuntius.app.extension.request

import icu.bluedream.nuntius.app.config.DeviceEnv
import okhttp3.Request

fun Request.Builder.setDeviceInfoHeaders() {
    this.apply {
        addHeader("x-rpc-device_fp", DeviceEnv.deviceFp)
        addHeader("x-rpc-device_id", DeviceEnv.deviceId)
        addHeader("x-rpc-device_model", "Xiaomi")
    }
}