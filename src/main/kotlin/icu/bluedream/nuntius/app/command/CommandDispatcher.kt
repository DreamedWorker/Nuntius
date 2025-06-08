package icu.bluedream.nuntius.app.command

import icu.bluedream.nuntius.app.QQMessageClient
import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.config
import icu.bluedream.nuntius.logger
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

object CommandDispatcher {
    private val masterCode by lazy {
        config.element("BotService").element("Master").text.toLong()
    }

    suspend fun dispatch(msgBody: MessageEvent, commandEntry: CommandEntry) {
        if (commandEntry.master) {
            if (msgBody.sender.userId != masterCode) {
                return
            }
        }
        try {
            val clazz = Class.forName(commandEntry.handler)
            val handler = clazz.getDeclaredConstructor().newInstance() as CommandAction
            handler.execute(msgBody)
        } catch (e: Exception) {
            val cmd = msgBody.message.first().data.text.drop(1)
            val errorMsgSendingStatus = QQMessageClient.sendTextMessage(
                "无法处理命令「${cmd}」，因为：${e.message}",
                msgBody
            )
            if (errorMsgSendingStatus.status == "ok") {
                delay(3.seconds)
                QQMessageClient.deleteMessage(errorMsgSendingStatus.data?.id?.toString())
            }
            logger.error("无法处理命令「${cmd}」，因为：${e.message}")
            e.printStackTrace()
        }
    }
}