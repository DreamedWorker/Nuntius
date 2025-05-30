package icu.bluedream.nuntius.app.config

import icu.bluedream.nuntius.configOperation
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

class Configuration() {
    private var properties: Properties = Properties()
    private var localFile: String

    init {
        val rootPath = configOperation["data_storage_path"] as String
        val filePath: File = if (rootPath == "/") {
            getLocalFile(System.getProperty("user.dir"))
        } else {
            getLocalFile(rootPath)
        }
        localFile = filePath.absolutePath
        with(properties) {
            load(FileInputStream(filePath))
        }
    }

    fun setValue(key: String, value: String) {
        properties.setProperty(key, value)
        properties.store(FileOutputStream(localFile), "properties")
    }

    fun getValue(key: String, default: String): String {
        val result = properties.getOrDefault(key, default) as String
        return result
    }

    private fun getLocalFile(root: String): File {
        val file = File(root, "config.properties")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }

    companion object {
        var instance: Configuration? = null

        fun getConfiguration(): Configuration {
            if (instance != null) {
                return instance!!
            } else {
                instance = Configuration()
                return instance!!
            }
        }
    }
}