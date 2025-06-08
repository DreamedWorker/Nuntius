package icu.bluedream.nuntius.app

import icu.bluedream.nuntius.app.data.MessageEvent
import org.dom4j.Element

class SecurityGuard(config: Element) {
    val allowedGroups: MutableList<Long> = mutableListOf()
    val blockedPersons: MutableList<Long> = mutableListOf()

    init {
        val groups = config.element("ServerGroups")
        groups.elementIterator().forEach { element ->
            if (element.name == "Code") {
                allowedGroups.add(element.text.toLong())
            }
        }
        val persons = config.element("BlockedPersons")
        persons.elementIterator().forEach { element ->
            if (element.name == "Code") {
                blockedPersons.add(element.text.toLong())
            }
        }
    }

    fun provideService(msg: MessageEvent): Boolean =
        allowedGroups.contains(msg.groupId) && !blockedPersons.contains(msg.sender.userId)
}