package icu.bluedream.nuntius.app.command

import icu.bluedream.nuntius.app.data.MessageEvent

interface CommandAction {
    suspend fun execute(msg: MessageEvent)
}