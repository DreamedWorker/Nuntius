package icu.bluedream.nuntius.app.util

import icu.bluedream.nuntius.LOGGER
import icu.bluedream.nuntius.configOperation
import java.io.File

object LocalPath {
    val workingPath by lazy {
        val prefix = configOperation["data_storage_path"] as String
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
            LOGGER.info("删除文件：${file}，状态：${file.delete()}")
        } else {
            LOGGER.warn("删除文件：${file}，但是其不存在！")
        }
    }

    fun getPathFile(prefix: File, child: String) = File(prefix, child)
}