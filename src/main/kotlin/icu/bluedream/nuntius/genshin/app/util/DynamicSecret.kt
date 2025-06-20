package icu.bluedream.nuntius.genshin.app.util

import java.security.MessageDigest

object DynamicSecret {
    const val K2 = "rtvTthKxEyreVXQCnhluFgLXPOFKPHlA"
    const val LK2 = "EJncUPGnOHajenjLhBOsdpwEMZmiCmQX"
    const val X4 = "xV8v4Qu54lUKrEYFZkJhB8cuOh9Asafs"
    const val X6 = "t0qEgfub6cvueAPgR5m9aQWWVciEer7v"
    const val PROD = "JwYDpKvLj6MrMqqYU6jTKF17KNO2PXoS"

    enum class SaltType {
        K2,
        LK2,
        X4,
        X6,
        PROD
    }

    enum class Version {
        Gen1,
        Gen2
    }

    fun getDynamicSecret(
        version: Version,
        saltType: SaltType,
        includeChars: Boolean = true,
        query: String = "",
        body: String = "",
    ): String {
        val salt = when (saltType) {
            SaltType.K2 -> K2
            SaltType.LK2 -> LK2
            SaltType.X4 -> X4
            SaltType.X6 -> X6
            SaltType.PROD -> PROD
        }
        val t = System.currentTimeMillis() / 1000L
        val r = if (includeChars) getRs1() else getRs2()
        var dsContent = "salt=${salt}&t=${t}&r=${r}"
        val q = query.split("&").sortedBy { it }
            .joinToString(separator = "&") { it }
        if (version == Version.Gen2) {
            dsContent = "${dsContent}&b=${if (saltType == SaltType.PROD) "{}" else body}&q=${q}"
        }
        val check = toMd5HexString(dsContent).lowercase()
        return "${t},${r},${check}"
    }

    private fun getRs2(): String = (100000..200000).random().toString()

    private fun getRs1(): String {
        val range = "abcdefghijklmnopqrstuvwxyz1234567890"
        val sb = StringBuffer()
        repeat(6) {
            sb.append(range.random())
        }
        return sb.toString()
    }

    private fun toMd5HexString(string: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(string.toByteArray())
        with(StringBuilder()) {
            digest.forEach {
                val hex = it.toInt() and (0xFF)
                val toHexString = Integer.toHexString(hex)
                if (toHexString.length == 1) {
                    this.append("0$toHexString")
                } else {
                    this.append(toHexString)
                }
            }
            return this.toString()
        }
    }
}