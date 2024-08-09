architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentFabric: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentFabric.extendsFrom(common)
}

dependencies {
    modApi("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    compileOnly("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowCommon(project(":common", "transformProductionFabric")) { isTransitive = false }

    modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")
    modApi("com.terraformersmc:modmenu:${rootProject.property("modmenu_version")}")
    modImplementation("foundry.veil:Veil-fabric-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")
    include ("foundry.veil:Veil-fabric-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")
    include ("com.jamieswhiteshirt:reach-entity-attributes:2.5.0")
    modApi("mezz.jei:jei-${rootProject.property("minecraft_version")}-fabric-api:${rootProject.property("jei_version")}")
    modImplementation("com.jamieswhiteshirt:reach-entity-attributes:2.4.0")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.1")
    modRuntimeOnly("maven.modrinth:yeetus-experimentus:2.3.1")
    modRuntimeOnly("maven.modrinth:spark:1.10.53-fabric")
    modRuntimeOnly("curse.maven:worldedit-225608:4586218")
    modRuntimeOnly("mezz.jei:jei-${rootProject.property("minecraft_version")}-fabric:${rootProject.property("jei_version")}")
}

tasks.shadowJar {
    exclude("architectury.common.json")
    configurations = listOf(shadowCommon)
    archiveBaseName.set(archiveBaseName.get() + "-fabric")
    archiveClassifier.set("dev-shadow")
    archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveBaseName.set(archiveBaseName.get() + "-fabric")
    archiveClassifier.set(null as String?)
    archiveVersion.set("${version}+${rootProject.property("minecraft_version")}")
}

tasks.jar {
    archiveBaseName.set(archiveBaseName.get() + "-fabric")
    archiveClassifier.set("dev-shadow")
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
