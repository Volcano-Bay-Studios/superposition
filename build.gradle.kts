import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    id("java")
    id("architectury-plugin") version "3.4-SNAPSHOT"
    id("dev.architectury.loom") version "1.6-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

architectury {
    minecraft = rootProject.property("minecraft_version").toString()
}

dependencies {
    subprojects
}

tasks.build.get().finalizedBy(tasks.named("shadowJar"))


tasks.processResources {
    val expandProps = mapOf(
        "group" to rootProject.property("maven_group"),
        "version" to project.version,

        "mod_id" to rootProject.property("mod_id"),
        "mod_name" to rootProject.property("mod_name"),
        "mod_license" to rootProject.property("mod_license"),
        "mod_description" to rootProject.property("mod_description"),
        "mod_authors" to rootProject.property("mod_authors"),
        "mod_version" to rootProject.property("mod_version"),
        "maven_group" to rootProject.property("maven_group"),

        "minecraft_version" to rootProject.property("minecraft_version"),
        "architectury_version" to rootProject.property("architectury_version"),
        "forge_loader_range" to rootProject.property("forge_loader_range"),
        "forge_version_range" to rootProject.property("forge_version_range"),

        "veil_version" to rootProject.property("veil_version"),
        "flywheel_version" to rootProject.property("flywheel_version"),
        "jei_version" to rootProject.property("jei_version"),
    )
    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "architectury-plugin")
    apply(plugin = "com.github.johnrengelman.shadow")

    base.archivesName.set(rootProject.property("archives_base_name").toString())
    version = rootProject.property("mod_version").toString()
    group = rootProject.property("maven_group").toString()

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "TerraformersMC Maven"
            url = uri("https://maven.terraformersmc.com/releases")
        }

        maven {
            name = "Jamie's White Shirt Maven"
            url = uri("https://maven.jamieswhiteshirt.com/libs-release")
        }

        maven {
            name = "DevAuth Maven"
            url = uri("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
        }

        maven {
            name = "ParchmentMC Maven"
            url = uri("https://maven.parchmentmc.org")
        }

        maven {
            name = "BlameJared Maven"
            url = uri("https://maven.blamejared.com")
        }

        maven {
            name = "Modrinth Maven"
            url = uri("https://api.modrinth.com/maven")
        }

        maven {
            name = "CurseForge Maven"
            url = uri("https://cursemaven.com")
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(17)
    }

    java {
        withSourcesJar()
    }

    tasks.processResources {
        val expandProps = mapOf(
            "group" to rootProject.property("maven_group"),
            "version" to project.version,

            "mod_id" to rootProject.property("mod_id"),
            "mod_name" to rootProject.property("mod_name"),
            "mod_license" to rootProject.property("mod_license"),
            "mod_description" to rootProject.property("mod_description"),
            "mod_authors" to rootProject.property("mod_authors"),
            "mod_version" to rootProject.property("mod_version"),
            "maven_group" to rootProject.property("maven_group"),

            "minecraft_version" to rootProject.property("minecraft_version"),
            "architectury_version" to rootProject.property("architectury_version"),
            "forge_loader_range" to rootProject.property("forge_loader_range"),
            "forge_version_range" to rootProject.property("forge_version_range"),

            "veil_version" to rootProject.property("veil_version"),
            "flywheel_version" to rootProject.property("flywheel_version"),
            "jei_version" to rootProject.property("jei_version"),
        )
        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
            expand(expandProps)
        }
        inputs.properties(expandProps)
    }

    tasks.register<Jar>("prodJar") {
        from(tasks.named("compileJava"))
        from(sourceSets.main.get().resources)

        if (project.name == rootProject.name) {
            archiveBaseName.set(archiveBaseName.get())
        } else {
            archiveBaseName.set(archiveBaseName.get() + "-" + project.name)
        }

        archiveClassifier.set(null as String?)
        archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
    }

    tasks.named("shadowJar").get().finalizedBy(tasks.named("prodJar"))
}

subprojects {
    apply(plugin = "dev.architectury.loom")

    val loom = project.extensions.getByName<LoomGradleExtensionAPI>("loom")

    repositories {
        mavenLocal()
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:${rootProject.property("minecraft_version")}")

        @Suppress("UnstableApiUsage")
        "mappings"(loom.layered {
            officialMojangMappings()
            parchment("org.parchmentmc.data:parchment-${rootProject.property("minecraft_version")}:${rootProject.property("parchment_version")}@zip")
        })
    }
}
