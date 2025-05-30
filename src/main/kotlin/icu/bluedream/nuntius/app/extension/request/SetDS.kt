package icu.bluedream.nuntius.app.extension.request

import icu.bluedream.nuntius.genshin.util.DynamicSecret
import okhttp3.Request

fun Request.Builder.setDS(
    version: DynamicSecret.Version,
    saltType: DynamicSecret.SaltType,
    includeChars: Boolean = false,
    query: String = "",
    body: String = "",
) {
    val build = this.build()
    val urls = build.url.toString().split("?")
    val b = if (saltType == DynamicSecret.SaltType.PROD) "{}" else body
    if (urls.size > 1) {
        val parameters = urls.last().split("&").sortedBy { it }.joinToString(separator = "&") { it }
        this.addHeader("DS",
            DynamicSecret.getDynamicSecret(version,
                saltType,
                includeChars,
                parameters,
                b))
    } else {
        this.addHeader("DS",
            DynamicSecret.getDynamicSecret(version,
                saltType,
                includeChars,
                query,
                b))
    }
}