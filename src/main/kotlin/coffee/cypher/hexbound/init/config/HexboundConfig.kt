package coffee.cypher.hexbound.init.config

import coffee.cypher.hexbound.init.Hexbound
import net.minecraft.util.Identifier

object HexboundConfig {
    val replaceSpiderConstruct: Boolean by configField()
    private val constructActionDenyList: List<String> by configField()

    private val constructActionDenyCache = mutableMapOf<Identifier, Boolean>()
    private var constructActionRegex: List<Regex>? = null

    fun isActionForbiddenForConstruct(action: Identifier): Boolean {
        if (action in constructActionDenyCache) {
            return constructActionDenyCache.getValue(action)
        }

        if (constructActionRegex == null) {
            constructActionRegex = constructActionDenyList.map {
                it.split('*').joinToString(".*", transform = Regex::escape, prefix = "^", postfix = "$").toRegex()
            }
        }

        val regexList = constructActionRegex!!

        val result = regexList.any { it.matches(action.toString()) }

        constructActionDenyCache[action] = result

        return result
    }

    val CONFIG = buildConfig(Hexbound.id("config")) {
        section("client") {
            value("force_alternate_construct") {
                comment("Force Spider Construct to always use the alternative Robot model")
                bind(::replaceSpiderConstruct)

                defaultValue(false)
            }
        }

        section("execution") {
            listValue("construct_action_deny_list") {
                comment("Identifiers of pattern actions that should never be executed by Constructs.")
                comment("By default, includes most spells (should a Construct somehow acquire media to cast one) and sending orders to other Constructs or Hexal Wisps.")
                comment("Additionally, any entry containing a star will match any text (including empty strings) in that position.")
                bind(::constructActionDenyList)

                fallbackValue("")

                defaultValue(
                    "hexbound:send_instructions",
                    "hexbound:*_colorizer",
                    "hexcasting:explode*",
                    "hexcasting:add_motion",
                    "hexcasting:blink",
                    "hexcasting:break_block",
                    "hexcasting:place_block",
                    "hexcasting:craft/*",
                    "hexcasting:recharge",
                    "hexcasting:erase",
                    "hexcasting:*_water",
                    "hexcasting:ignite",
                    "hexcasting:extinguish",
                    "hexcasting:conjure_*",
                    "hexcasting:bonemeal",
                    "hexcasting:edify",
                    "hexcasting:colorize",
                    "hexcasting:sentinel/*",
                    "hexcasting:potion/*",
                    "hexal:everbook/*",
                    "hexal:wisp/summon/*",
                    "hexal:wisp/seon/*",
                    "hexal:wisp/consume",
                    "hexal:smelt",
                    "hexal:place_type",
                    "hexal:freeze",
                    "hexal:falling_block"
                )
            }
        }
    }

    fun init() {
        CONFIG.registerCallback {
            constructActionDenyCache.clear()
            constructActionRegex = null
        }
    }
}
