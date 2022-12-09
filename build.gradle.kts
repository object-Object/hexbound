@file:Suppress("UnstableApiUsage", "UNCHECKED_CAST")

import groovy.json.JsonSlurper
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL
import coffee.cypher.gradleutil.filters.*

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    `maven-publish`
    signing
    alias(libs.plugins.kotlin)
    alias(libs.plugins.quilt.loom)
    alias(libs.plugins.dokka)
    alias(libs.plugins.nexus)
    alias(libs.plugins.serialization)
}

base {
    @Suppress("DEPRECATION")
    archivesBaseName = rootProject.name
}

val modProps = JsonSlurper().parseText(File("mod.json").readText()) as Map<String, Any>

version = (modProps.getValue("core") as Map<String, Any>).getValue("version")
group = "coffee.cypher"

repositories {
    mavenCentral()

    maven("https://maven.quiltmc.org/repository/release") {
        name = "Quilt"
    }
    maven("https://maven.quiltmc.org/repository/snapshot") {
        name = "Quilt Snapshots"
    }

    maven("https://maven.blamejared.com") {
        name = "BlameJared"
    }
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
    maven("https://mvn.devos.one/snapshots/")
    maven("https://maven.jamieswhiteshirt.com/libs-release/")
    maven("https://maven.shedaniel.me/")
    maven("https://maven.terraformersmc.com/") {
        name = "TerraformersMC"
    }
    maven("https://ladysnake.jfrog.io/artifactory/mods") {
        name = "Ladysnake Libs"
    }

    maven("https://jitpack.io") {
        name = "JitPack"
    }

    maven("https://maven.cafeteria.dev/releases") {
        content {
            includeGroup("dev.cafeteria")
        }
    }

    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(
        variantOf(libs.quilt.mappings) {
            classifier("intermediary-v2")
        }
    )
    modImplementation(libs.quilt.loader)

    modImplementation(libs.bundles.kotlin)

    modImplementation(libs.bundles.qsl)
    modImplementation(libs.bundles.hexcasting) {
        exclude(module = "fabric-language-kotlin")
        exclude(module = "phosphor")
    }
    modImplementation(libs.cca)
    modImplementation(libs.kettle)

    modImplementation(libs.fake.player)
    include(libs.fake.player)

    modImplementation(libs.mixin.extras)
    annotationProcessor(libs.mixin.extras)
    include(libs.mixin.extras)

    modImplementation(libs.geckolib)

    modCompileOnly(libs.hexal)
    //modLocalRuntime(libs.hexal)
}

val javaVersion = JavaVersion.VERSION_17

loom {
    runs {
        named("client") {
            vmArgs("-Dmixin.debug.export=true")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
    }

    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion

    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion.toString()))
    }
}

tasks {
    processResources {
        inputs.property("version", version)

        filesMatching("quilt.mod.json") {
            expand(mapOf("version" to version))
        }

        filesMatching("**/*.flatten.json5") {
            filter(FlatteningJsonFilter::class.java)
            path = path.replace("\\.flatten\\.json5$".toRegex(), ".json")
        }
    }

    withType<KotlinCompile> {
        kotlinOptions {
            useK2 = false
            jvmTarget = javaVersion.toString()
            freeCompilerArgs =
                listOf("-Xenable-builder-inference")
        }
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release.set(javaVersion.toString().toInt())
    }

    jar {
        from("LICENSE") {
            rename { "${it}_${project.name}" }
        }
    }

    dokkaJekyll.configure {
        enabled = false

        description = "Generates GitHub Pages reference for the project"

        outputDirectory.set(project.buildDir.resolve("docs/reference"))

        doFirst {
            delete(project.buildDir.resolve("docs/reference"))
        }
    }

    dokkaJavadoc.configure {
        description = "Generates Javadoc for the project"

        outputDirectory.set(this@tasks.javadoc.map { it.destinationDir!! })
    }

    withType<DokkaTask> {
        group = "documentation"

        dokkaSourceSets {
            configureEach {
                sourceLink {
                    localDirectory.set(projectDir.resolve("src/main/kotlin"))
                    remoteLineSuffix.set("#L")
                    remoteUrl.set(URL("https://github.com/Cypher121/hex-magia/blob/master/src/main/kotlin"))
                }

                jdkVersion.set(java.toolchain.languageVersion.get().asInt())

                reportUndocumented.set(true)
            }
        }
    }

    javadoc {
        dependsOn(dokkaJavadoc)

        taskActions.clear()
    }

    register("buildUserGuide", Copy::class) {
        group = "documentation"
        dependsOn(dokkaJekyll)

        from(projectDir.resolve("docs"))
        into(project.buildDir.resolve("docs"))
    }
}

//region publishing

tasks {
    dokkaJavadoc {
        onlyIf { !project.hasProperty("publishOnly") }
    }

    javadoc {
        onlyIf { !project.hasProperty("publishOnly") }
    }

    remapJar {
        onlyIf { !project.hasProperty("publishOnly") }
    }

    remapSourcesJar {
        onlyIf { !project.hasProperty("publishOnly") }
    }
}

class Keystore(project: Project) {
    val pgpKey: String? by project
    val pgpPassword: String? by project

    val sonatypeUsername: String? by project
    val sonatypePassword: String? by project
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = group.toString()
            artifactId = project.name
            version = version.toString()

            from(components["java"])

            pom {
                name.set("Hex-Magia")
                description.set("HexCasting addon")
                url.set("https://www.modrinth.com/mod/hex-magia")

                scm {
                    connection.set("scm:git:git://github.com/Cypher121/hex-magia.git")
                    developerConnection.set("scm:git:ssh://github.com/Cypher121/hex-magia.git")
                    url.set("https://github.com/Cypher121/hex-magia/")
                }

                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("cypher121")
                        name.set("Cypher121")
                    }
                }
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

nexusPublishing {
    repositories {
        sonatype {
            username.set(Keystore(project).sonatypeUsername.orEmpty())
            password.set(Keystore(project).sonatypePassword.orEmpty())
        }
    }
}

signing {
    sign(publishing.publications)

    val keystore = Keystore(project)

    if (keystore.pgpKey != null) {
        useInMemoryPgpKeys(
            keystore.pgpKey,
            keystore.pgpPassword
        )
    }
}

tasks.register("prepareArtifacts", Copy::class) {
    group = "publishing"

    val artifactDir: String? by project

    dependsOn(tasks.remapJar, tasks.javadoc, tasks.remapSourcesJar)

    from(project.buildDir.resolve("libs")) {
        include("**/*.jar")
    }

    val destination = artifactDir?.let(::File)
                      ?: project.buildDir.resolve("release")

    into(destination)
}

//endregion publishing
