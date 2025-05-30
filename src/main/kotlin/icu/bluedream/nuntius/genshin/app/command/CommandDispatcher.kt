package icu.bluedream.nuntius.genshin.app.command

import icu.bluedream.nuntius.LOGGER
import icu.bluedream.nuntius.cq.NTMessageSender
import icu.bluedream.nuntius.cq.model.MessageEvent
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class CommandDispatcher(private val commandConfig: CommandConfig) {
    suspend fun dispatch(msg: MessageEvent, command: String, master: String) {
        val commandEntry = commandConfig.commands[command] ?: return

        try {
            val clazz = Class.forName(commandEntry.handler)
            val handler = clazz.getDeclaredConstructor().newInstance() as CommandAction
            if (commandEntry.master) {
                require(msg.sender.userId.toString() == master) {
                    "此命令仅限主人调用！"
                }
            }
            handler.execute(msg)
        } catch (e: Exception) {
            val msg = NTMessageSender.sendTextMessage(
                msg.groupId.toString(),
                "无法处理命令「${command}」，因为：${e.message}"
            )
            if (msg.status == "ok") {
                delay(3.seconds)
                NTMessageSender.deleteMessage(msg.data?.id?.toString())
            }
            LOGGER.error("无法处理命令「${command}」，因为：${e.message}")
            e.printStackTrace()
        }
    }
}