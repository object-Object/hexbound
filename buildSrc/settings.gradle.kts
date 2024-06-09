dependencyResolutionManagement {
    // allow referencing the main project's version catalog
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
