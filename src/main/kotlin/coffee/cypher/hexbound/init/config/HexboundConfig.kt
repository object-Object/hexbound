package coffee.cypher.hexbound.init.config

import coffee.cypher.hexbound.init.Hexbound

object HexboundConfig {
    val replaceSpiderConstruct: Boolean by configField()

    val CONFIG = buildConfig(Hexbound.id("config")) {
        section("client") {
            value("enable_alternate_construct") {
                comment("Replaces model and display name for Spider Construct with more neutral ones")

                defaultValue = false
                bind(::replaceSpiderConstruct)
            }
        }
    }
}
