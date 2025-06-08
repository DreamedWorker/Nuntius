package icu.bluedream.nuntius.genshin.app.database

import icu.bluedream.nuntius.config
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccountEntity
import icu.bluedream.nuntius.genshin.impl.account.HoyoAccounts
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
        val stored = config.element("StorageConfiguration").element("DataPath").text
        File(
            if (stored == "/") System.getProperty("user.dir") else stored,
            "plugin_gi_database"
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