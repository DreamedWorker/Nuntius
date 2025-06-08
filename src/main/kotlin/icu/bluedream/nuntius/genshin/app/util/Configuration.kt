package icu.bluedream.nuntius.genshin.app.util

import cn.hutool.core.io.FileUtil
import cn.hutool.core.util.CharsetUtil
import cn.hutool.setting.Setting
import icu.bluedream.nuntius.config
import java.io.File

object Configuration {
    private val pluginConfig by lazy {
        val storagePath = config.element("StorageConfiguration").element("DataPath").text
        val file = if (storagePath == "/") File(System.getProperty("user.dir"), "genshin.settings")
        else File(storagePath, "genshin.settings")
        Setting(FileUtil.touch(file), CharsetUtil.CHARSET_UTF_8, true)
    }

    init {
        pluginConfig.autoLoad(true)
    }

    fun getStringValue(key: String, def: String): String = pluginConfig.getStr(key, def)

    fun setValue(key: String, value: String) {
        pluginConfig.set(key, value)
        pluginConfig.store()
    }
}