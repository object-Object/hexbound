package coffee.cypher.hexbound.init

import coffee.cypher.hexbound.init.config.HexboundConfig
import coffee.cypher.hexbound.interop.InteropManager
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.loader.api.QuiltLoader
import org.quiltmc.qkl.library.brigadier.execute
import org.quiltmc.qkl.library.brigadier.register
import org.quiltmc.qkl.library.brigadier.util.sendFeedback
import org.quiltmc.qkl.library.commands.onCommandRegistration
import org.quiltmc.qkl.library.registerEvents
import org.quiltmc.qkl.library.text.buildText
import org.quiltmc.qkl.library.text.literal
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer

object Hexbound : ModInitializer {
    lateinit var MOD_ID: String
    lateinit var LOGGER: Logger

    fun id(name: String): Identifier {
        return Identifier(MOD_ID, name)
    }

    override fun onInitialize(mod: ModContainer) {
        MOD_ID = mod.metadata().id()
        LOGGER = LogManager.getLogger(MOD_ID)

        HexboundConfig.init()

        initCommonRegistries()
        HexboundPatterns.register()
        InteropManager.init()

        if (QuiltLoader.isDevelopmentEnvironment()) {
            enableDebugFeatures()
        }
    }

    private fun enableDebugFeatures() {
        registerEvents {
            onCommandRegistration { _, _ ->
                register("getConstructCommands") {
                    execute {
                        sendFeedback(buildText {
                            CommonRegistries.CONSTRUCT_COMMANDS.ids.forEach {
                                literal("[$it -> ${CommonRegistries.CONSTRUCT_COMMANDS.get(it)}]\n")
                            }
                        })
                    }
                }
            }
        }
    }
}
