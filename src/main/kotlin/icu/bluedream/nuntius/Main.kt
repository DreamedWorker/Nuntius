package icu.bluedream.nuntius

import icu.bluedream.nuntius.app.BotConfig
import icu.bluedream.nuntius.app.LocalPath
import icu.bluedream.nuntius.app.SecurityGuard
import icu.bluedream.nuntius.app.WebsocketClient
import icu.bluedream.nuntius.app.command.CommandDispatcher
import icu.bluedream.nuntius.app.command.CommandEntry
import icu.bluedream.nuntius.app.command.CommandLoader
import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.genshin.GenshinEntrypoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeoutOrNull
import org.dom4j.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

val logger: Logger = LoggerFactory.getLogger("Nuntius")
val config: Element = BotConfig.readConfig().rootElement
val userTaskMutexMap = ConcurrentHashMap<Long, Mutex>()
val loadedCommands: MutableMap<String, CommandEntry> = mutableMapOf()

fun main() {
    LocalPath.checkDirs()
    val securityGuard = SecurityGuard(config.element("BotService"))
    logger.info("哇~世界灿烂盛大！")
    val callingToken = config.element("BotService").element("CallingToken").text
    loadedCommands.apply {
        putAll(CommandLoader.loadCommands(
            Thread.currentThread().contextClassLoader.getResourceAsStream("genshin/commands.xml")!!
        ))
    }
    GenshinEntrypoint.beforeStart()
    WebsocketClient.connectToServer(
        webConfig = config.element("BotNetwork"),
        service = { messageEvent, isPublic ->
            val textMessage = messageEvent.message.first().data.text
            if (textMessage.startsWith(callingToken)) {
                val commandName = textMessage.drop(1)
                if (loadedCommands.keys.contains(commandName)) {
                    val cmdEntry = loadedCommands[commandName]!!
                    if (isPublic) {
                        if (securityGuard.provideService(messageEvent)) {
                            startTask(messageEvent, cmdEntry)
                        }
                    } else {
                        startTask(messageEvent, cmdEntry)
                    }
                } else {
                    logger.warn("用户呼叫了一个不存在的命令：${textMessage}")
                }
            }
        }
    )
}

private fun startTask(msg: MessageEvent, entry: CommandEntry) {
    val userMutex = userTaskMutexMap.computeIfAbsent(msg.sender.userId) { Mutex() }
    CoroutineScope(Dispatchers.IO).launch {
        if (!userMutex.tryLock()) {
            logger.warn("用户 ${msg.sender.userId} 的任务已在进行中，忽略本次。")
            return@launch
        }
        try {
            withTimeoutOrNull(TimeUnit.SECONDS.toMillis(300)) {
                CommandDispatcher.dispatch(msg, entry)
            } ?: logger.warn("用户 ${msg.sender.userId} 的本次任务已经超时！")
        } finally {
            userMutex.unlock()
        }
    }
}