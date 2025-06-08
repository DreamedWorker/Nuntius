package icu.bluedream.nuntius.genshin.app.extension.request

import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.request.header

//fun Request.Builder.setXRpcAppInfo(
//    clientType: String = "2",
//    appId: String = "bll8iq97cem8"
//) {
//    addHeader("x-rpc-app_version", "2.71.1")
//    addHeader("x-rpc-client_type", clientType)
//    addHeader("x-rpc-app_id", appId)
//}

fun DefaultRequest.DefaultRequestBuilder.setXRpcAppInfoCIO(
    clientType: String = "2",
    appId: String = "bll8iq97cem8"
) {
    header("x-rpc-app_version", "2.71.1")
    header("x-rpc-client_type", clientType)
    header("x-rpc-app_id", appId)
}

fun DefaultRequest.DefaultRequestBuilder.setXRpcChallenge(value:String) = this.header("x-rpc-challenge",value)

fun DefaultRequest.DefaultRequestBuilder.setXRpcClientType(value:String) = this.header("x-rpc-client_type",value)

fun DefaultRequest.DefaultRequestBuilder.setXRpcAigis(value:String) = this.header("x-rpc-aigis",value)