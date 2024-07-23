architectury {
    platformSetupLoomIde()
    forge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)

    forge.apply {
        mixinConfig("superposition-common.mixins.json")
        mixinConfig("superposition.mixins.json")

        convertAccessWideners.set(true)
        extraAccessWideners.add(loom.accessWidenerPath.get().asFile.name)
    }
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentForge: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentForge.extendsFrom(common)
}

dependencies {
    forge("net.minecraftforge:forge:${rootProject.property("forge_version")}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionForge")) { isTransitive = false }

    modApi("dev.architectury:architectury-forge:${rootProject.property("architectury_version")}")
    modImplementation("foundry.veil:Veil-forge-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")
    modApi("mezz.jei:jei-${rootProject.property("minecraft_version")}-forge-api:${rootProject.property("jei_version")}")

    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-latest:1.2.1")
    modRuntimeOnly("maven.modrinth:yeetus-experimentus:build.4+mc1.20.1")
    modRuntimeOnly("maven.modrinth:spark:1.10.53-forge")
    modRuntimeOnly("curse.maven:worldedit-225608:4586218")
    modRuntimeOnly("mezz.jei:jei-${rootProject.property("minecraft_version")}-forge:${rootProject.property("jei_version")}")
}

tasks.shadowJar {
    exclude("fabric.mod.json")
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveBaseName.set(archiveBaseName.get() + "-forge")
    archiveClassifier.set("dev-shadow")
    archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveBaseName.set(archiveBaseName.get() + "-forge")
    archiveClassifier.set(null as String?)
    archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
}

tasks.jar {
    archiveBaseName.set(archiveBaseName.get() + "-forge")
    archiveClassifier.set("dev")
    archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
}

tasks.sourcesJar {
    val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })
}

components.getByName("java") {
    this as AdhocComponentWithVariants
    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}