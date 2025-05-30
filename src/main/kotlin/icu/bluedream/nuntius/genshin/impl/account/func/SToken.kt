package icu.bluedream.nuntius.genshin.impl.account.func

import icu.bluedream.nuntius.app.web.buildPostRequest
import icu.bluedream.nuntius.app.web.defaultHoyoClient
import icu.bluedream.nuntius.app.web.getAsStruct
import icu.bluedream.nuntius.app.web.toRequestBody
import icu.bluedream.nuntius.genshin.ApiEndpoints
import icu.bluedream.nuntius.genshin.impl.account.LoginException
import icu.bluedream.nuntius.genshin.model.account.QRCodeToken
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys
import org.json.JSONObject

@Throws(LoginException::class)
suspend fun getSTokenByGameToken(gameToken: QRCodeToken): STokenStruct.STokenInfo.STData {
    val reqBody = with(JSONObject()) {
        put("account_id", gameToken.uid.toInt())
        put("game_token", gameToken.token)
        toString().toRequestBody()
    }
    val data = buildPostRequest(
        {
            url(ApiEndpoints.getTokenByGameToken())
        },
        reqBody
    ).getAsStruct<STokenStruct.STokenInfo>(defaultHoyoClient)
    if (data.retcode != 0) {
        throw LoginException("无法获取你的stoken和mid，因为：${data.message}")
    } else {
        return data.data
    }
}

object STokenStruct {
    @OptIn(ExperimentalSerializationApi::class)
    @Serializable
    @JsonIgnoreUnknownKeys
    data class STokenInfo(
        val retcode: Int,
        val message: String,
        val data: STData
    ) {
        @Serializable
        @JsonIgnoreUnknownKeys
        data class STData(
            val token: SToken,
            @SerialName("user_info") val user: UserInfo
        ) {
            @Serializable
            @JsonIgnoreUnknownKeys
            data class SToken(
                val token: String
            )

            @Serializable
            @JsonIgnoreUnknownKeys
            data class UserInfo(
                val aid: String,
                val mid: String
            )
        }
    }
}