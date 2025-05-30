package icu.bluedream.nuntius.genshin.app.command

import icu.bluedream.nuntius.cq.model.MessageEvent

interface CommandAction {
    suspend fun execute(msg: MessageEvent)
}