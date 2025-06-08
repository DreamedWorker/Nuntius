package icu.bluedream.nuntius.genshin.app.model.account

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class CookieToken(
    val retcode: Long,
    val message: String,
    val data: CookieToken
) {
    @Serializable
    @JsonIgnoreUnknownKeys
    data class CookieToken(
        @SerialName("cookie_token") val cookieToken: String
    )
}
