
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("eclipse")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.0.1"
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("xyz.jpenilla.run-paper") version "1.0.6"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.1"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.dokka") version "1.7.20"
}

group = "dev.nikomaru.raceassist"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
    maven("https://repo.incendo.org/content/repositories/snapshots")
}

val cloudVersion = "1.7.1"
val exposedVersion = "0.38.2"
val ktorVersion = "2.1.1"
dependencies {


    compileOnly("io.papermc.paper", "paper-api", "1.19-R0.1-SNAPSHOT")

    library(kotlin("stdlib"))

    compileOnly("com.github.MilkBowl", "VaultAPI", "1.7")

    implementation("cloud.commandframework", "cloud-core", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-extensions", cloudVersion)
    implementation("cloud.commandframework", "cloud-paper", cloudVersion)
    implementation("cloud.commandframework", "cloud-annotations", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-coroutines-annotations", cloudVersion)
    implementation("cloud.commandframework", "cloud-kotlin-coroutines", cloudVersion)

    implementation("com.github.stefvanschie.inventoryframework", "IF", "0.10.6")

    implementation("mysql", "mysql-connector-java", "8.0.30")

    implementation("io.ktor", "ktor-server-core", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("io.ktor", "ktor-server-content-negotiation", ktorVersion)
    implementation("io.ktor", "ktor-serialization-kotlinx-json", ktorVersion)
    implementation("io.ktor", "ktor-server-auth", ktorVersion)
    implementation("io.ktor", "ktor-server-auth-jwt", ktorVersion)
    implementation("io.ktor", "ktor-network-tls-certificates", ktorVersion)
    implementation("io.ktor", "ktor-client-core", ktorVersion)
    implementation("io.ktor", "ktor-client-java", ktorVersion)
    implementation("io.ktor", "ktor-client-logging", ktorVersion)
    implementation("io.ktor", "ktor-client-content-negotiation", ktorVersion)

    implementation("ch.qos.logback", "logback-classic", "1.2.11")

    implementation("org.jetbrains.exposed", "exposed-core", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-dao", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-jdbc", exposedVersion)
    implementation("org.jetbrains.exposed", "exposed-java-time", exposedVersion)

    implementation("org.jetbrains.kotlinx", "kotlinx-datetime", "0.4.0")

    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.6.4")

    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-json", "1.3.3")
    implementation("org.jetbrains.kotlinx", "kotlinx-serialization-hocon", "1.3.3")

    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-api", "2.7.0")
    implementation("com.github.shynixn.mccoroutine", "mccoroutine-bukkit-core", "2.7.0")

    implementation("com.google.api-client", "google-api-client", "1.35.1")
    implementation("com.google.oauth-client", "google-oauth-client-jetty", "1.34.1")
    implementation("com.google.apis", "google-api-services-sheets", "v4-rev20220606-1.32.1")

    implementation(kotlin("stdlib-jdk8"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

val pluginGroup = "dev.nikomaru.raceassist.core"

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "17"
        kotlinOptions.javaParameters = true
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "17"
    }
    shadowJar {
        relocate("cloud.commandframework", "$pluginGroup.shaded.cloud")
        relocate("io.leangen.geantyref", "$pluginGroup.shaded.typetoken")
        relocate("com.github.stefvanschie.inventoryframework", "$pluginGroup.inventoryframework")
    }
    build {
        dependsOn(shadowJar)
    }
    runServer {
        minecraftVersion("1.19.3")
    }
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
}


bukkit {
    name = "RaceAssist"
    //minecraft_plugin_version
    version = "minecraft_plugin_version"
    website = "https://docs-raceassist.nikomaru.dev/"

    main = "dev.nikomaru.raceassist.RaceAssist"

    apiVersion = "1.19"
    depend = listOf("Vault")
    libraries = listOf(
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-api:2.7.0",
        "com.github.shynixn.mccoroutine:mccoroutine-bukkit-core:2.7.0"
    )


    permissions {
        register("RaceAssist.admin") {
            default = Default.OP
            children = listOf(
                "raceassist.commands.audience.leave",
                "raceassist.commands.place.degree",
                "raceassist.commands.bet.sheet",
                "raceassist.commands.race.start",
                "raceassist.commands.player.add",
                "raceassist.commands.horse.ownerdelete",
                "raceassist.commands.bet.can",
                "raceassist.commands.setting.copy",
                "raceassist.commands.player.remove",
                "raceassist.commands.bet.list",
                "raceassist.commands.bet.delete",
                "raceassist.commands.setting.view",
                "raceassist.commands.place.central",
                "raceassist.commands.audience.list",
                "raceassist.commands.setting.staff",
                "raceassist.commands.setting.create",
                "raceassist.commands.bet.open",
                "raceassist.commands.place.reverse",
                "raceassist.commands.bet.revert.jockey",
                "raceassist.commands.reload",
                "raceassist.commands.web",
                "raceassist.commands.place.set",
                "raceassist.commands.player.delete",
                "raceassist.commands.player.list",
                "raceassist.commands.race.debug",
                "raceassist.commands.race.horse",
                "raceassist.commands.bet.revert.row",
                "raceassist.commands.place.finish",
                "raceassist.commands.bet.return.jockey",
                "raceassist.commands.setting.delete",
                "raceassist.commands.race.stop",
                "raceassist.commands.bet.unit",
                "raceassist.commands.bet.revert.all",
                "raceassist.commands.place.lap",
                "raceassist.commands.bet.rate",
                "raceassist.commands.player.replacement",
                "raceassist.commands.audience.join",
                "raceassist.command.help"
            )
        }
        register("RaceAssist.user") {
            default = Default.TRUE
            children = listOf(
                "raceassist.commands.audience.join",
                "raceassist.commands.audience.leave",
                "raceassist.commands.bet.open",
                "raceassist.commands.web",
                "raceassist.commands.horse.ownerdelete",
            )
        }
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

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        configureEach {
            perPackageOption {
                matchingRegex.set(".*")
                suppress.set(true)
            }
            perPackageOption {
                matchingRegex.set("dev.nikomaru.raceassist.api.core.*")
                suppress.set(false)
            }
            perPackageOption {
                matchingRegex.set(".*\\.data.*")
                suppress.set(false)
            }
        }
    }
}


tasks.register("depsize") {
    description = "Prints dependencies for \"default\" configuration"
    doLast {
        listConfigurationDependencies(configurations["default"])
    }
}

tasks.register("depsize-all-configurations") {
    description = "Prints dependencies for all available configurations"
    doLast {
        configurations
            .filter { it.isCanBeResolved }
            .forEach { listConfigurationDependencies(it) }
    }
}

fun listConfigurationDependencies(configuration: Configuration) {
    val formatStr = "%,10.2f"

    val size = configuration.map { it.length() / (1024.0 * 1024.0) }.sum()

    val out = StringBuffer()
    out.append("\nConfiguration name: \"${configuration.name}\"\n")
    if (size > 0) {
        out.append("Total dependencies size:".padEnd(65))
        out.append("${String.format(formatStr, size)} Mb\n\n")

        configuration.sortedBy { -it.length() }
            .forEach {
                out.append(it.name.padEnd(65))
                out.append("${String.format(formatStr, (it.length() / 1024.0))} kb\n")
            }
    } else {
        out.append("No dependencies found")
    }
    println(out)
}
