package icu.bluedream.nuntius.genshin

import icu.bluedream.nuntius.LOGGER
import icu.bluedream.nuntius.app.config.DeviceEnv
import icu.bluedream.nuntius.cq.model.MessageEvent
import icu.bluedream.nuntius.genshin.app.command.CommandDispatcher
import icu.bluedream.nuntius.genshin.app.command.CommandLoader

val commandConfig = CommandLoader.loadCommands()
val dispatcher = CommandDispatcher(commandConfig)

object GenshinEntrypoint {
    suspend fun beforeStart() {
        try {
            DeviceEnv.checkDeviceEnv()
            DeviceEnv.checkDeviceFp()
        } catch (e: Exception) {
            LOGGER.warn("注意：获取（或更新）设备指纹出错，这可能导致后续部分接口失效！${e.message}")
        }
    }

    suspend fun genshinPoint(msg: MessageEvent, trigger: String, master: String) {
        val cmd = msg.message.first().data.text
        if (cmd.startsWith(trigger)) {
            dispatcher.dispatch(msg, cmd.drop(1), master)
        } else {
            return
        }
    }
}