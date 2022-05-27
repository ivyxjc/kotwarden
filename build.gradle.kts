import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kodeinVersion = "7.11.0"
val dynamodbSdkVersion = "2.17.192"
val mapstructVersion = "1.5.0.RC1"
val bouncyCastleVersion = "1.71"

plugins {
    application
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    kotlin("kapt") version "1.6.21"
    id("org.graalvm.buildtools.native") version "0.9.4"
    id("com.github.johnrengelman.shadow") version "7.1.2"

}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

group = "com.ivyxjc"
version = "0.0.1"
application {
    mainClass.set("com.ivyxjc.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
    maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jwt-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-cio:$ktor_version")
    implementation("io.ktor:ktor-server-resources:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("org.kodein.di:kodein-di:$kodeinVersion")

    implementation("org.bouncycastle:bcprov-jdk18on:$bouncyCastleVersion")

    implementation(project.dependencies.platform("software.amazon.awssdk:bom:$dynamodbSdkVersion"))
    implementation("software.amazon.awssdk:dynamodb:$dynamodbSdkVersion")
    implementation("software.amazon.awssdk:dynamodb-enhanced:$dynamodbSdkVersion")

    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.6.1")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")

    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapstructVersion")

    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    implementation(kotlin("stdlib-jdk8"))
}
kapt {
    arguments {
    }
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}