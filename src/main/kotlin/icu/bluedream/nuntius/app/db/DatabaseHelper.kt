package icu.bluedream.nuntius.app.db

import icu.bluedream.nuntius.configOperation
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccountEntity
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.cookieToken
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.gameToken
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.genshinNickname
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.genshinPicID
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.genshinUID
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.level
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.ltoken
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.mid
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.qqCode
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.serverName
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.serverRegion
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.stoken
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts.stuid
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.io.File

object DatabaseHelper {
    fun insertHoyoAccount(account: HoyoAccountEntity): Boolean {
        val a = transaction(connection) {
            HoyoAccounts.insert {
                it[qqCode] = account.qqCode
                it[cookieToken] = account.cookieToken
                it[gameToken] = account.gameToken
                it[ltoken] = account.ltoken
                it[mid] = account.mid
                it[stoken] = account.stoken
                it[stuid] = account.stuid
                it[genshinNickname] = account.genshinNicname
                it[genshinPicID] = account.genshinPicID
                it[genshinUID] = account.genshinUID
                it[level] = account.level
                it[serverName] = account.serverName
                it[serverRegion] = account.serverRegion
            }
        }
        return a.insertedCount == 1
    }

    private val dbFile by lazy {
        val stored = configOperation["data_storage_path"] as String
        File(
            if (stored == "/") System.getProperty("user.dir") else stored,
            "database"
        )
    }

    val connection: Database = Database.connect(
        url = "jdbc:h2:${dbFile.absolutePath}",
        driver = "org.h2.Driver"
    )

    init {
        transaction(connection) {
            SchemaUtils.create(HoyoAccounts)
        }
    }
}