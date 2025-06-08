package icu.bluedream.nuntius.genshin.impl.account

import icu.bluedream.nuntius.genshin.app.model.account.CookieToken
import icu.bluedream.nuntius.genshin.app.model.account.QRCodeToken
import icu.bluedream.nuntius.genshin.app.web.ApiEndpoints
import icu.bluedream.nuntius.genshin.app.web.HoyoWebClient
import io.ktor.client.call.body
import io.ktor.client.request.get


suspend fun getCookieToken(token: QRCodeToken): CookieToken {
    val result = HoyoWebClient.hoyoClient.get(
        "${ApiEndpoints.getCookieToken()}?account_id=${token.uid}&game_token=${token.token}"
    ).body<CookieToken>()
//    val result = buildRequest {
//        url("${ApiEndpoints.getCookieToken()}?account_id=${token.uid}&game_token=${token.token}")
//    }.getAsStruct<CookieToken>()
    if (result.retcode != 0L) {
        throw LoginException("我们无法获取ck，${result.message}")
    }
    return result
}