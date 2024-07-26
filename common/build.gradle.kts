import net.fabricmc.loom.task.RemapJarTask

tasks.register<RemapJarTask>("remapJar") {
    nestedJars.setFrom()
}

architectury {
    common(rootProject.property("enabled_platforms").toString().split(","))
}

loom {
    accessWidenerPath.set(file("src/main/resources/superposition.accesswidener"))
}

dependencies {
    compileOnly("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    compileOnly("foundry.veil:Veil-mojmap-${rootProject.property("minecraft_version")}:${rootProject.property("veil_version")}")

    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")
    modApi("mezz.jei:jei-${rootProject.property("minecraft_version")}-common-api:${rootProject.property("jei_version")}")
}

tasks.processResources {
    inputs.property("version", version)
}
