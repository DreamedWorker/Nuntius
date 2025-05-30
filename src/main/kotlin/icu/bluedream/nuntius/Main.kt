package icu.bluedream.nuntius

import icu.bluedream.nuntius.app.config.BotConfig
import icu.bluedream.nuntius.app.config.Configuration
import icu.bluedream.nuntius.app.db.DatabaseHelper
import icu.bluedream.nuntius.app.util.LocalPath
import icu.bluedream.nuntius.app.util.SecurityUtil
import icu.bluedream.nuntius.app.web.WebsocketHelper
import icu.bluedream.nuntius.genshin.GenshinEntrypoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeoutOrNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

val LOGGER: Logger = LoggerFactory.getLogger("Nuntius")

val config = BotConfig.loadBotConfig() // 读取配置文件
@Suppress("UNCHECKED_CAST")
val configOperation = config["operation"] as Map<String, Any>
@Suppress("UNCHECKED_CAST")
val configBot = config["bot"] as Map<String, Any>
@Suppress("UNCHECKED_CAST")
val configService = config["service"] as Map<String, Any>

suspend fun main() {
    LocalPath.checkDirs()
    val userTaskMutexMap = ConcurrentHashMap<Long, Mutex>()
    LOGGER.info(DatabaseHelper.connection.toString()) // 连接本地数据库
    // 初始化配置文件管理器
    LOGGER.info(Configuration.getConfiguration().getValue("deviceFp", ""))
    // 设置安全组
    SecurityUtil.createInstance(configService)
    //// 插件初始化入口
    GenshinEntrypoint.beforeStart()
    // 结束插件初始化
    LOGGER.info("哇~世界灿烂盛大！")
    WebsocketHelper.websocketHelper(
        configBot
    ) { msg ->
        if (SecurityUtil.instance.shouldProvideServices(
                senderID = msg.sender.userId.toString(),
                groupId = msg.groupId.toString()
            )) {
            val userMutex = userTaskMutexMap.computeIfAbsent(msg.sender.userId) { Mutex() }
            CoroutineScope(Dispatchers.IO).launch {
                if (!userMutex.tryLock()) {
                    LOGGER.warn("用户 ${msg.sender.userId} 的任务已在进行中，忽略本次。")
                    return@launch
                }
                try {
                    val trigger = configOperation["calling_token"] as String
                    val master = configService["master_qq"] as String
                    withTimeoutOrNull(TimeUnit.SECONDS.toMillis(300)) {
                        runBlocking {
                            val  startTime = System.currentTimeMillis()
                            val task = async { GenshinEntrypoint.genshinPoint(msg, trigger, master) }
                            task.await()
                            val endTime = System.currentTimeMillis()
                            LOGGER.info("用户 ${msg.sender.userId} 的任务已完成，耗时：${endTime - startTime}")
                        }
                    } ?: LOGGER.warn("用户 ${msg.sender.userId} 的本次任务已经超时！")
                } finally {
                    userMutex.unlock()
                }
            }
        }
    }
}