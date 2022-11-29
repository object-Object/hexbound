package coffee.cypher.hexbound.init.config

import coffee.cypher.hexbound.init.Hexbound

object HexboundConfig {
    val replaceSpiderConstruct: Boolean by configField()

    val CONFIG = buildConfig(Hexbound.id("config")) {
        section("client") {
            value("force_alternate_construct") {
                comment("Force Spider Construct to always use the alternative Robot model")

                defaultValue = false
                bind(::replaceSpiderConstruct)
            }
        }
    }
}
