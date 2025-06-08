package icu.bluedream.nuntius.genshin.app.extension.request

import icu.bluedream.nuntius.genshin.app.util.DynamicSecret
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.header

fun DefaultRequest.DefaultRequestBuilder.setDS(
    url: String,
    version: DynamicSecret.Version,
    saltType: DynamicSecret.SaltType,
    includeChars: Boolean = false,
    query: String = "",
    body: String = "",
) {
    val urls = url.split("?")
    val b = if (saltType == DynamicSecret.SaltType.PROD) "{}" else body
    if (urls.size > 1) {
        val parameters = urls.last().split("&").sortedBy { it }.joinToString(separator = "&") { it }
        this.header("DS",
            DynamicSecret.getDynamicSecret(version,
                saltType,
                includeChars,
                parameters,
                b))
    } else {
        this.header("DS",
            DynamicSecret.getDynamicSecret(version,
                saltType,
                includeChars,
                query,
                b))
    }
}