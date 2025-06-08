package icu.bluedream.nuntius.genshin.commands.uid

import icu.bluedream.nuntius.app.command.CommandAction
import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.genshin.impl.account.BindingUidJob

class BindingUidCommand : CommandAction {
    override suspend fun execute(msg: MessageEvent) {
        BindingUidJob.bindingUid(msg)
    }
}