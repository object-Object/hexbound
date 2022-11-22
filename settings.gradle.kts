import groovy.json.JsonSlurper

val modProps = JsonSlurper().parseText(File("mod.json").readText()) as Map<String, Any>

rootProject.name = (modProps.getValue("core") as Map<String, Any>).getValue("name") as String

pluginManagement {
    repositories {
        maven("https://maven.quiltmc.org/repository/release") {
            name = "Quilt"
        }
        maven("https://maven.quiltmc.org/repository/snapshot") {
            name = "Quilt Snapshots"
        }
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        gradlePluginPortal()
    }
}
