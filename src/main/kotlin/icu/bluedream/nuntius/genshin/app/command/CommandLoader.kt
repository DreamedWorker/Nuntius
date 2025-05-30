package icu.bluedream.nuntius.genshin.app.command

import org.yaml.snakeyaml.Yaml

object CommandLoader {
    @Suppress("UNCHECKED_CAST")
    fun loadCommands(): CommandConfig {
        val inputStream = Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream("genshin/commands.yaml")
        val yaml = Yaml()
        val temp = yaml.load<Map<String, Any>>(inputStream)
        val commands = (temp["commands"] as LinkedHashMap<String, LinkedHashMap<String, Any>>)
        val maps = mutableMapOf<String, CommandConfig.CommandEntry>()
        commands.forEach { t ->
            maps.put(t.key, CommandConfig.CommandEntry(
                description = t.value["description"]!! as String,
                handler = t.value["handler"]!! as String,
                master = (t.value["master"]!! as Int) == 1
            ))
        }
        val result = CommandConfig(
            commands = maps
        )
        return result
    }
}