package icu.bluedream.nuntius.app.extension.file

import java.nio.file.Files
import java.nio.file.Paths
import java.util.Base64

fun String.img2base64(): String {
    val path = Paths.get(this)
    val bytes = Files.readAllBytes(path)
    return Base64.getEncoder().encodeToString(bytes)
}