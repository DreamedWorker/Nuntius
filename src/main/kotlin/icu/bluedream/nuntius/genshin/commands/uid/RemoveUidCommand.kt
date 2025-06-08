package icu.bluedream.nuntius.genshin.commands.uid

import icu.bluedream.nuntius.app.QQMessageClient
import icu.bluedream.nuntius.app.command.CommandAction
import icu.bluedream.nuntius.app.data.MessageEvent
import icu.bluedream.nuntius.genshin.app.database.DatabaseHelper
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts
import kotlinx.coroutines.delay
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Duration.Companion.seconds

class RemoveUidCommand : CommandAction {
    override suspend fun execute(msg: MessageEvent) {
        val accounts = transaction(DatabaseHelper.connection) {
            HoyoAccounts.selectAll().limit(1)
                .where { HoyoAccounts.qqCode eq msg.sender.userId.toString() }
                .toList()
        }
        require(accounts.isNotEmpty()) { "该账号没有绑定UID。" }
        val account = accounts.first()
        val uid = account[HoyoAccounts.genshinUID]
        val count = transaction(DatabaseHelper.connection) {
            HoyoAccounts.deleteWhere(1) {
                HoyoAccounts.genshinUID eq uid
            }
        }
        require(count == 1) { "执行删除操作时出现异常。" }
        val stateMsg = QQMessageClient.sendTextMessage(
            "我们已经为${msg.sender.userId}删除了UID「${uid}」。", msg)
        delay(3.seconds)
        QQMessageClient.deleteMessage(stateMsg.data?.id?.toString())
    }
}