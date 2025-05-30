package icu.bluedream.nuntius.genshin.impl.account

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table

data class HoyoAccountEntity(
    var qqCode: String,
    var cookieToken: String,
    var gameToken: String,
    var ltoken: String,
    var mid: String,
    var stoken: String,
    var stuid: String,
    var genshinNicname: String,
    var genshinPicID: String,
    var genshinUID: String,
    var level: String,
    var serverName: String,
    var serverRegion: String,
)

object HoyoAccounts : Table("hoyo_accounts") {
    val qqCode = varchar("qq_code", 20) // QQ号
    val cookieToken = varchar("cookie_token", 255)
    val gameToken = varchar("game_token", 255)
    val ltoken = varchar("ltoken", 255)
    val mid = varchar("mid", 50)
    val stoken = varchar("stoken", 255)
    val stuid = varchar("stuid", 50)
    val genshinNickname = varchar("genshin_nickname", 100)
    val genshinPicID = varchar("genshin_pic_id", 50)
    val genshinUID = varchar("genshin_uid", 20)
    val level = varchar("level", 10)
    val serverName = varchar("server_name", 100)
    val serverRegion = varchar("server_region", 100)

    override val primaryKey = PrimaryKey(qqCode)
}

fun ResultRow.toHoyoAccountEntity() = HoyoAccountEntity(
    qqCode = this[HoyoAccounts.qqCode],
    cookieToken = this[HoyoAccounts.cookieToken],
    gameToken = this[HoyoAccounts.gameToken],
    ltoken = this[HoyoAccounts.ltoken],
    mid = this[HoyoAccounts.mid],
    stoken = this[HoyoAccounts.stoken],
    stuid = this[HoyoAccounts.stuid],
    genshinNicname = this[HoyoAccounts.genshinNickname],
    genshinPicID = this[HoyoAccounts.genshinPicID],
    genshinUID = this[HoyoAccounts.genshinUID],
    level = this[HoyoAccounts.level],
    serverName = this[HoyoAccounts.serverName],
    serverRegion = this[HoyoAccounts.serverRegion]
)