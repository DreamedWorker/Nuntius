package icu.bluedream.nuntius.genshin.model.account

import kotlinx.serialization.Serializable

@Serializable
data class LToken(
    val retcode: Long,
    val message: String,
    val data: LTokenData
) {
    @Serializable
    data class LTokenData(
        val ltoken: String
    )
}
