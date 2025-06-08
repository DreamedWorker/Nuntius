plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    application
}

group = "icu.bluedream"
version = "1.0-SNAPSHOT"
val ktor_version: String by project
val logback_version: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1") // 协程支持
    // 序列化工具
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.json:json:20250517")
    implementation("org.dom4j:dom4j:2.1.4")
    // 网络框架
    implementation("io.ktor:ktor-client-core:${ktor_version}")
    //implementation("io.ktor:ktor-client-okhttp:${ktor_version}")
    implementation("io.ktor:ktor-client-cio:${ktor_version}")
    implementation("io.ktor:ktor-client-websockets:${ktor_version}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktor_version}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktor_version}")
    // 本地数据库和连接工具
    implementation("com.h2database:h2:2.3.232")
    implementation("org.jetbrains.exposed:exposed-core:1.0.0-beta-2")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0-beta-2")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-2")
    implementation("net.glxn:qrgen:1.4") // 二维码工具
    implementation("ch.qos.logback:logback-classic:${logback_version}") // 日志工具
    implementation("cn.hutool:hutool-all:5.8.38")
}

application {
    mainClass = "icu.bluedream.nuntius.MainKt"
}

kotlin {
    jvmToolchain(21)
}