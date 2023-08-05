package coffee.cypher.hexbound.init.config

import coffee.cypher.hexbound.init.Hexbound
import net.minecraft.util.Identifier

object HexboundConfig {
    val replaceSpiderConstruct: Boolean by configField()
    val spiderBatteryChargeRequired: Int by configField()
    val broadcasterParticleAmount: Int by configField()
    val constructPathfindingAttempts: Int by configField()

    val CONFIG = buildConfig(Hexbound.id("config")) {
        section("client") {
            value("force_alternate_construct") {
                comment("Force Spider Construct to always use the alternative Robot model")
                bind(::replaceSpiderConstruct)

                defaultValue(false)
            }

            value("broadcaster_particle_amount") {
                comment("How many particles an activated Construct Broadcaster fires off")
                bind(::broadcasterParticleAmount)

                defaultValue(16)

                allowedRange(0..200)
            }
        }

        section("misc") {
            value("spider_battery_charge") {
                comment("Changes the required media charge for a Spider Construct Battery to fully charge (in amethyst dust units)")
                bind(::spiderBatteryChargeRequired)

                defaultValue(50)

                allowedRange(1..Int.MAX_VALUE)
            }

            value("construct_pathfinding_attempts") {
                comment("If a construct fails to get reasonably close to the target, it can try pathfinding to it again.")
                comment("Increasing this value will make constructs reaching their target more consistent, but can lead to trying to reach unreachable targets for too long, or cause lag.")
                bind(::constructPathfindingAttempts)

                defaultValue(3)

                allowedRange(1..10)
            }
        }
    }

    fun init() {

    }
}
