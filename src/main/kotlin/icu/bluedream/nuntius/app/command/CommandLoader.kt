package icu.bluedream.nuntius.app.command

import org.dom4j.io.SAXReader
import java.io.InputStream

object CommandLoader {
    fun loadCommands(`is`: InputStream): Map<String, CommandEntry> {
        val cmdList: MutableMap<String, CommandEntry> = mutableMapOf()
        val commandsRoot = SAXReader().read(`is`).rootElement
        commandsRoot.elementIterator().forEach { element ->
            if (element.name == "Command") {
                cmdList.put(
                    element.attribute("name").text,
                    CommandEntry(
                        description = element.attribute("description").text,
                        handler = element.attribute("handler").text,
                        master = element.attribute("master").text == "1"
                    )
                )
            }
        }
        return cmdList
    }
}