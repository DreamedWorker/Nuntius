package icu.bluedream.nuntius.genshin.impl.account.func

import icu.bluedream.nuntius.app.web.buildRequest
import icu.bluedream.nuntius.app.web.getAsStruct
import icu.bluedream.nuntius.genshin.ApiEndpoints
import icu.bluedream.nuntius.genshin.impl.account.LoginException
import icu.bluedream.nuntius.genshin.model.account.CookieToken
import icu.bluedream.nuntius.genshin.model.account.QRCodeToken


suspend fun getCookieToken(token: QRCodeToken): CookieToken {
    val result = buildRequest {
        url("${ApiEndpoints.getCookieToken()}?account_id=${token.uid}&game_token=${token.token}")
    }.getAsStruct<CookieToken>()
    if (result.retcode != 0L) {
        throw LoginException("我们无法获取ck，${result.message}")
    }
    return result
}