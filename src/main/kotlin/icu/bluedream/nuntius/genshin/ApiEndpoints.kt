package icu.bluedream.nuntius.genshin

object ApiEndpoints {
    private const val PUBLIC_DATA_API = "https://public-data-api.mihoyo.com"
    private const val HK4E_SDK = "https://hk4e-sdk.mihoyo.com"
    private const val TAKUMI_API = "https://api-takumi.mihoyo.com"
    private const val PASSPORT_API = "https://passport-api.mihoyo.com"

    fun getFp() = "${PUBLIC_DATA_API}/device-fp/api/getFp"

    fun getQRFetch() = "${HK4E_SDK}/hk4e_cn/combo/panda/qrcode/fetch"
    fun getQRQuery() = "${HK4E_SDK}/hk4e_cn/combo/panda/qrcode/query"

    fun getTokenByGameToken() = "${TAKUMI_API}/account/ma-cn-session/app/getTokenByGameToken"
    fun getCookieToken() = "${TAKUMI_API}/auth/api/getCookieAccountInfoByGameToken"
    fun getGameBasic() = "${TAKUMI_API}/binding/api/getUserGameRolesByStoken"
    fun getLToken() = "${PASSPORT_API}/account/auth/api/getLTokenBySToken"
}