architectury {
    platformSetupLoomIde()
    neoForge()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentNeoForge: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentNeoForge.extendsFrom(common)
}

dependencies {
//    forge("net.minecraftforge:forge:${rootProject.property("minecraft_version")}-${rootProject.property("forge_version")}")
    neoForge("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionNeoForge")) { isTransitive = false }

    modApi("dev.architectury:architectury-neoforge:${rootProject.property("architectury_version")}"){ isTransitive = false }
    modImplementation("foundry.veil:veil-neoforge-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}") { isTransitive = false; exclude(group = "maven.modrinth") }
    include("foundry.veil:veil-neoforge-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}") { isTransitive = false; exclude(group = "maven.modrinth") }
    modApi("mezz.jei:jei-${rootProject.property("minecraft_version")}-forge-api:${rootProject.property("jei_version")}")

//    modRuntimeOnly("me.djtheredstoner:DevAuth-neoforge-latest:1.2.1")
    //modRuntimeOnly("maven.modrinth:spark:1.10.53-forge")
    //modRuntimeOnly("curse.maven:worldedit-225608:4586218")
    //modRuntimeOnly("mezz.jei:jei-${rootProject.property("minecraft_version")}-forge:${rootProject.property("jei_version")}")
}

tasks.shadowJar {
    exclude("fabric.mod.json")
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveBaseName.set(archiveBaseName.get() + "-neoforge")
    archiveClassifier.set("dev-shadow")
    archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveBaseName.set(archiveBaseName.get() + "-neoforge")
    archiveClassifier.set(null as String?)
    archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
}

tasks.jar {
    archiveBaseName.set(archiveBaseName.get() + "-neoforge")
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