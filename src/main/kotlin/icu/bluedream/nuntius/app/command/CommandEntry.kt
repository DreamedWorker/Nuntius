package icu.bluedream.nuntius.app.command

data class CommandEntry(
    val description: String,
    val handler: String,
    val master: Boolean
)
