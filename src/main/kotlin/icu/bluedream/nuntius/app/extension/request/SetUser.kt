package icu.bluedream.nuntius.app.extension.request

import okhttp3.Request

fun Request.Builder.setUser(a: String, b: String, c: String) {
    this.apply {
        addHeader("cookie", "stuid=${a};stoken=${b};mid=${c}")
    }
}