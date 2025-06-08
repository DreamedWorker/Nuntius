package icu.bluedream.nuntius.genshin.app.extension.request

import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.header

fun DefaultRequest.DefaultRequestBuilder.setUser(a: String, b: String, c: String) {
    this.apply {
        header("cookie", "stuid=${a};stoken=${b};mid=${c}")
    }
}