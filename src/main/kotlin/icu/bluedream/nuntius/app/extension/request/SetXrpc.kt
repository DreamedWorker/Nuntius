package icu.bluedream.nuntius.app.extension.request

import okhttp3.Request

fun Request.Builder.setXRpcAppInfo(
    clientType: String = "2",
    appId: String = "bll8iq97cem8"
) {
    addHeader("x-rpc-app_version", "2.71.1")
    addHeader("x-rpc-client_type", clientType)
    addHeader("x-rpc-app_id", appId)
}

fun Request.Builder.setXRpcChallenge(value:String) = this.addHeader("x-rpc-challenge",value)

fun Request.Builder.setXRpcClientType(value:String) = this.header("x-rpc-client_type",value)

fun Request.Builder.setXRpcAigis(value:String) = this.header("x-rpc-aigis",value)