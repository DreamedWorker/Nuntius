package icu.bluedream.nuntius.genshin.app.extension.request

import icu.bluedream.nuntius.genshin.impl.DeviceEnv
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.header

//fun Request.Builder.setDeviceInfoHeaders() {
//    this.apply {
//        addHeader("x-rpc-device_fp", DeviceEnv.deviceFp)
//        addHeader("x-rpc-device_id", DeviceEnv.deviceId)
//        addHeader("x-rpc-device_model", "Xiaomi")
//    }
//}

fun DefaultRequest.DefaultRequestBuilder.setDeviceInfoHeadersCIO() {
    this.apply {
        header("x-rpc-device_fp", DeviceEnv.deviceFp)
        header("x-rpc-device_id", DeviceEnv.deviceId)
        header("x-rpc-device_model", "Xiaomi")
    }
}