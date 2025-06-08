package icu.bluedream.nuntius.genshin.app.web

import icu.bluedream.nuntius.genshin.app.extension.request.setDeviceInfoHeadersCIO
import icu.bluedream.nuntius.genshin.app.extension.request.setXRpcAppInfoCIO
import icu.bluedream.nuntius.genshin.impl.DeviceEnv
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HoyoWebClient {
//    val hoyoClientOld by lazy {
//        HttpClient(OkHttp) {
//            engine {
//                addInterceptor {
//                    val request = it.request().newBuilder()
//                    request.addHeader("x-rpc-sys_version", "13")
//                    request.addHeader("x-rpc-channel", "miyousheluodi")
//                    request.addHeader("User-Agent", DeviceEnv.HOYO_MOBILE_UA)
//                    request.setXRpcAppInfo()
//                    request.setDeviceInfoHeaders()
//                    it.proceed(request.build())
//                }
//            }
//            install(ContentNegotiation) {
//                json(Json { ignoreUnknownKeys = true })
//            }
//        }
//    }

    val hoyoClient by lazy {
        HttpClient(CIO) {
            install(DefaultRequest) {
                header("x-rpc-sys_version", "13")
                header("x-rpc-channel", "miyousheluodi")
                header("User-Agent", DeviceEnv.HOYO_MOBILE_UA)
                setXRpcAppInfoCIO()
                setDeviceInfoHeadersCIO()
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
}