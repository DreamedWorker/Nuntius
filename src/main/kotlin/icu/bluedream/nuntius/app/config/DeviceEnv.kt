package icu.bluedream.nuntius.app.config

import icu.bluedream.nuntius.LOGGER
import icu.bluedream.nuntius.app.model.DeviceFingerprint
import icu.bluedream.nuntius.app.util.AutoCheckedKey
import icu.bluedream.nuntius.app.web.buildPostRequest
import icu.bluedream.nuntius.app.web.defaultHoyoClient
import icu.bluedream.nuntius.app.web.getAsStruct
import icu.bluedream.nuntius.app.web.toRequestBody
import icu.bluedream.nuntius.genshin.ApiEndpoints
import org.json.JSONObject
import java.util.Date
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object DeviceEnv : AutoCheckedKey("deviceFpLastCheck", false) {
    const val HOYO_MOBILE_UA =
        "Mozilla/5.0 (Linux; Android 13; Xiaomi Build/OPR6.170623.012) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/95.0.4638.74 Mobile Safari/537.36 miHoYoBBS/2.71.1"

    val deviceId by lazy {
        Configuration.Companion.getConfiguration().getValue("deviceId", "")
    }

    val deviceFp by lazy {
        Configuration.Companion.getConfiguration().getValue("deviceFp", "")
    }

    private val bbsDeviceId by lazy {
        Configuration.Companion.getConfiguration().getValue("bbsDeviceId", "")
    }

    @OptIn(ExperimentalUuidApi::class)
    fun checkDeviceEnv() {
        if (deviceId == "") {
            Configuration.Companion.getConfiguration().setValue("deviceId", Uuid.Companion.random().toString().lowercase())
            println("")
        }
        if (bbsDeviceId == "") {
            Configuration.Companion.getConfiguration().setValue("bbsDeviceId", Uuid.Companion.random().toString().lowercase())
        }
    }

    suspend fun checkDeviceFp(requiredByMaster: Boolean = false): String {
        try {
            return if (requiredByMaster) {
                fetchFromNetwork()
            } else {
                if (shouldFetchFromNetwork) {
                    fetchFromNetwork()
                } else {
                    if (deviceFp != "") {
                        deviceFp
                    } else {
                        fetchFromNetwork()
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.warn("无法获取（或更新）你的设备指纹，因为：${e.message}")
            return ""
        }
    }

    @Throws(IllegalStateException::class)
    private suspend fun fetchFromNetwork(): String {
        val body = generateRequestBody()
        val result = buildPostRequest(
            {
                url(ApiEndpoints.getFp())
            },
            body.toRequestBody()
        ).getAsStruct<DeviceFingerprint>(defaultHoyoClient)
        println(result)
        val fp = result.data.deviceFP
        Configuration.Companion.getConfiguration().setValue("deviceFp", fp)
        storeFetch(Date())
        LOGGER.info("好耶！我们成功获取（或更新）了你的设备指纹--${fp}")
        return fp
    }

    private fun getUpperAndNumberString(length: Int): String {
        return with(StringBuilder()) {
            val base = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            repeat(length) {
                append(base.random())
            }
            this.toString()
        }
    }

    private fun getLowerHexString(length: Int): String {
        return with(StringBuilder()) {
            val base = "0123456789abcdef"
            repeat(length) {
                append(base.random())
            }
            this.toString()
        }
    }

    private fun generateRequestBody(): String {
        val device = getUpperAndNumberString(12)
        val product = getUpperAndNumberString(6)
        val oldFp = deviceFp

        val ext = JSONObject().apply {
            put("oaid", "")
            put("vaid", "")
            put("aaid", "")
            put("serialNumber", "unknown")
            put("board", "taro")
            put("brand", "XiaoMi")
            put("hardware", "qcom")
            put("cpuType", "arm64-v8a")
            put("deviceType", "OP5913L1")
            put("display", "${product}_13.1.0.181(CN01)")
            put("hostname", "dg02-pool03-kvm87")
            put("manufacturer", "XiaoMi")
            put("productName", product)
            put("model", device)
            put("deviceInfo", "XiaoMi/$product/OP5913L1:13/SKQ1.221119.001/T.118e6c7-5aa23-73911:user/release-keys")
            put("sdkVersion", "34")
            put("osVersion", "14")
            put("devId", "REL")
            put("buildTags", "release-keys")
            put("buildType", "user")
            put("buildUser", "android-build")
            put("buildTime", "1693626947000")
            put("screenSize", "1440x2905")
            put("vendor", "unknown")
            put("romCapacity", "512")
            put("romRemain", "512")
            put("ramCapacity", "469679")
            put("ramRemain", "239814")
            put("appMemory", "512")
            put("accelerometer", "1.4883357x7.1712894x6.2847486")
            put("gyroscope", "0.030226856x0.014647375x0.010652636")
            put("magnetometer", "20.081251x-27.487501x2.1937501")
            put("isRoot", 0)
            put("debugStatus", 1)
            put("proxyStatus", 0)
            put("emulatorStatus", 0)
            put("isTablet", 0)
            put("simState", 5)
            put("ui_mode", "UI_MODE_TYPE_NORMAL")
            put("sdCapacity", "512215")
            put("sdRemain", "239600")
            put("hasKeyboard", 0)
            put("isMockLocation", 0)
            put("ringMode", 2)
            put("isAirMode", 0)
            put("batteryStatus", 100)
            put("chargeStatus", 1)
            put("deviceName", device)
            put("appInstallTimeDiff", 1688455751496)
            put("appUpdateTimeDiff", 1702604034482)
            put("packageName", "com.mihoyo.hyperion")
            put("packageVersion", "2.71.1")
            put("networkType", "WiFi")
        }
        val allDic = JSONObject().apply {
            put("device_id", getLowerHexString(16))
            put("seed_id", getLowerHexString(16))
            put("platform", "2")
            put("seed_time", "${System.currentTimeMillis()}")
            put("ext_fields", ext.toString())
            put("app_name", "bbs_cn")
            put("bbs_device_id", bbsDeviceId)
            put("device_fp", oldFp.ifEmpty { getLowerHexString(13) })
        }

        return allDic.toString()
    }
}