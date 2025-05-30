package icu.bluedream.nuntius.app.web

import icu.bluedream.nuntius.app.config.DeviceEnv
import icu.bluedream.nuntius.app.extension.request.setDeviceInfoHeaders
import icu.bluedream.nuntius.app.extension.request.setXRpcAppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.jvm.Throws

val blankClient: OkHttpClient by lazy {
    OkHttpClient.Builder().build()
}

val defaultHoyoClient: OkHttpClient by lazy {
    OkHttpClient.Builder().apply {
        retryOnConnectionFailure(false)

        addInterceptor {
            val request = it.request().newBuilder()
            request.addHeader("x-rpc-sys_version", "13")
            request.addHeader("x-rpc-channel", "miyousheluodi")
            request.addHeader("User-Agent", DeviceEnv.HOYO_MOBILE_UA)
            request.setXRpcAppInfo()
            request.setDeviceInfoHeaders()
            it.proceed(request.build())
        }
    }.build()
}

fun buildRequest(block: Request.Builder.() -> Unit) = Request.Builder().apply(block).build()
fun buildPostRequest(
    block: Request.Builder.() -> Unit,
    body: RequestBody
) = Request.Builder().apply(block).post(body).build()
fun String.toRequestBody() = this.toRequestBody("application/json".toMediaType())

@Throws(IllegalStateException::class)
suspend inline fun <reified T> Request.getAsStruct(client: OkHttpClient = blankClient) =
    withContext(Dispatchers.IO) {
        try {
            val result = client.newCall(this@getAsStruct).execute().body?.string()
            if (result == null) {
                throw IllegalStateException("获取来自 Http 的响应时发生未知问题")
            }
            Json.decodeFromString<T>(result)
        } catch (e: Exception) {
            throw IllegalStateException("解析服务器返回的信息时出现错误：${e.message}")
        }
    }

@Throws(IllegalStateException::class)
suspend fun Request.getAsString(client: OkHttpClient = blankClient) =
    withContext(Dispatchers.IO) {
        try {
            val result = client.newCall(this@getAsString).execute().body?.string()
            if (result == null) {
                throw IllegalStateException("获取来自 Http 的响应时发生未知问题")
            }
            result
        } catch (e: Exception) {
            throw IllegalStateException("获取来自 Http 的响应时发生错误：${e.message}")
        }
    }