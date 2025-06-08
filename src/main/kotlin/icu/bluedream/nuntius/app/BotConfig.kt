package icu.bluedream.nuntius.app

import icu.bluedream.nuntius.logger
import org.dom4j.Document
import org.dom4j.io.SAXReader
import java.io.File
import kotlin.system.exitProcess

object BotConfig {
    fun readConfig(): Document {
        val configFile = File(System.getProperty("user.dir"), "nuntius.xml")
        if (configFile.exists()) {
            val reader = SAXReader()
            return reader.read(configFile)!!
        } else {
            logger.error("我们没有在你的工作目录${System.getProperty("user.dir")}下找到配置文件。")
            exitProcess(-1)
        }
    }
}