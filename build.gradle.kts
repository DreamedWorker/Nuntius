plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.serialization") version "2.1.20"
    application
}

group = "icu.bluedream"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1") // 协程支持
    implementation("org.yaml:snakeyaml:2.4") // 配置文件解析工具
    // 序列化工具
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    implementation("org.json:json:20250517")
    implementation("com.squareup.okhttp3:okhttp:4.12.0") // 网络框架
    // 本地数据库和连接工具
    implementation("com.h2database:h2:2.3.232")
    implementation("org.jetbrains.exposed:exposed-core:1.0.0-beta-2")
    implementation("org.jetbrains.exposed:exposed-dao:1.0.0-beta-2")
    implementation("org.jetbrains.exposed:exposed-jdbc:1.0.0-beta-2")
    implementation("net.glxn:qrgen:1.4") // 二维码工具
    // 日志工具
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.13")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "icu.bluedream.nuntius.MainKt"
}