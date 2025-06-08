package icu.bluedream.nuntius.genshin.commands

import icu.bluedream.nuntius.app.QQMessageClient
import icu.bluedream.nuntius.app.command.CommandAction
import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.genshin.impl.DeviceEnv
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class FingerprintCommand : CommandAction {
    override suspend fun execute(msg: MessageEvent) {
        val fp = DeviceEnv.fetchFromNetwork()
        val msg = QQMessageClient.sendTextMessage(
            "我们刚刚更新你的设备指纹到「${fp}」了。",
            msg
        )
        if (msg.status == "ok") {
            delay(5.seconds)
            QQMessageClient.deleteMessage(msg.data?.id?.toString())
        }
    }
}