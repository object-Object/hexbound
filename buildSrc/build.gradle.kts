plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://maven.blamejared.com/") }
}

dependencies {
    implementation(libs.pkpcpbp)
}
