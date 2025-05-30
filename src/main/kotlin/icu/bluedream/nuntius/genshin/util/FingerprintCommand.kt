package icu.bluedream.nuntius.genshin.util

import icu.bluedream.nuntius.app.config.DeviceEnv
import icu.bluedream.nuntius.cq.NTMessageSender
import icu.bluedream.nuntius.cq.model.MessageEvent
import icu.bluedream.nuntius.genshin.app.command.CommandAction
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Suppress("unused")
class FingerprintCommand : CommandAction {
    override suspend fun execute(msg: MessageEvent) {
        val fp = DeviceEnv.checkDeviceFp(true)
        val msg = NTMessageSender.sendTextMessage(
            msg.groupId.toString(),
            "我们刚刚更新你的设备指纹到「${fp}」了。"
        )
        if (msg.status == "ok") {
            delay(5.seconds)
            NTMessageSender.deleteMessage(msg.data?.id?.toString())
        }
    }
}