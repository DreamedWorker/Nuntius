package icu.bluedream.nuntius.genshin.impl.account

import icu.bluedream.nuntius.cq.model.MessageEvent
import icu.bluedream.nuntius.genshin.app.command.CommandAction

@Suppress("unused")
class AccountCommand : CommandAction {
    override suspend fun execute(msg: MessageEvent) {
        BindingUidJob.bindingUid(msg.groupId.toString(), msg.sender.userId.toString())
    }
}