package icu.bluedream.nuntius.app.config

import icu.bluedream.nuntius.LOGGER
import org.yaml.snakeyaml.Yaml
import java.io.File
import kotlin.system.exitProcess

object BotConfig {
    fun loadBotConfig(): Map<String, Any> {
        val file = File(System.getProperty("user.dir"), "nuntius.yaml")
        if (!file.exists()) {
            LOGGER.warn("我们不得不停止运行，因为找不到配置文件。")
            exitProcess(-1)
        }
        val yaml = Yaml()
        val data = yaml.load<Map<String, Any>>(file.inputStream())
        return data
    }
}