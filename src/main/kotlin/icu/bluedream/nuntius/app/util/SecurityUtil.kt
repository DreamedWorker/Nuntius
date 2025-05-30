package icu.bluedream.nuntius.app.util

import icu.bluedream.nuntius.LOGGER
import kotlin.system.exitProcess


@Suppress("UNCHECKED_CAST")
class SecurityUtil private constructor(operations: Map<String, Any>) {
    private val groups: List<String> = operations["allowed_groups"] as List<String>
    private val persons: List<String> = operations["blocked_person"] as List<String>

    fun shouldProvideServices(senderID: String, groupId: String): Boolean {
        if (groups.contains(groupId)) {
            return !persons.contains(senderID)
        }
        return false
    }

    companion object {
        var mInstance: SecurityUtil? = null

        val instance: SecurityUtil
            get() {
                if (mInstance == null) {
                    LOGGER.error("你的http客户端在初始化时出现了未知的问题！")
                    exitProcess(-3)
                } else {
                    return mInstance!!
                }
            }

        fun createInstance(config: Map<String, Any>) {
            mInstance = SecurityUtil(config)
        }
    }
}