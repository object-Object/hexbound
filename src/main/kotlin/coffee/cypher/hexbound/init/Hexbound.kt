package coffee.cypher.hexbound.init

import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.quiltmc.loader.api.ModContainer
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

        initCommonRegistries()
        HexboundPatterns.register()
    }
}
