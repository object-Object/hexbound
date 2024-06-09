package coffee.cypher.gradleutil

import at.petrak.pkpcpbp.filters.FlatteningJson5Transmogrifier

// https://github.com/gamma-delta/PKPCPBP/blob/786194a590/src/main/java/at/petrak/pkpcpbp/PKSubprojPlugin.java#L84
tasks.withType<ProcessResources>().configureEach {
    outputs.upToDateWhen { false }

    filesMatching("**/*.flatten.json5") {
        path = path.replace(".flatten.json5", ".json")
        filter<FlatteningJson5Transmogrifier>()
    }
}
