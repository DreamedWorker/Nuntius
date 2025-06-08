package icu.bluedream.nuntius.app

import icu.bluedream.nuntius.config
import icu.bluedream.nuntius.logger
import java.io.File
import kotlin.text.get

object LocalPath {
    val workingPath by lazy {
        val prefix = config.element("StorageConfiguration").element("DataPath").text
        if (prefix == "/") {
            File(System.getProperty("user.dir"))
        } else {
            File(prefix)
        }
    }
    val tempDir by lazy {
        File(workingPath, "Repeater")
    }

    fun checkDirs() {
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
    }

    fun createEmptyFile(file: String) {
        File(file).createNewFile()
    }

    fun deleteFile(file: String) {
        val file = File(file)
        if (file.exists()) {
            logger.info("删除文件：${file}，状态：${file.delete()}")
        } else {
            logger.warn("删除文件：${file}，但是其不存在！")
        }
    }

    fun getPathFile(prefix: File, child: String) = File(prefix, child)
}