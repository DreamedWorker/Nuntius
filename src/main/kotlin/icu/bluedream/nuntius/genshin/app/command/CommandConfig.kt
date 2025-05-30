package icu.bluedream.nuntius.genshin.app.command

data class CommandConfig(
    val commands: Map<String, CommandEntry>
) {
    data class CommandEntry(
        val description: String,
        val handler: String,
        val master: Boolean
    )
}
