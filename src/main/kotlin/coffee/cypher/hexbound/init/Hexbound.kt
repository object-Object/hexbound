package coffee.cypher.hexbound.init

import at.petrak.hexcasting.api.item.HexHolderItem
import at.petrak.hexcasting.api.spell.iota.ListIota
import at.petrak.hexcasting.common.lib.HexItems
import coffee.cypher.hexbound.feature.item_patterns.iota.ItemStackIota
import coffee.cypher.hexbound.init.config.HexboundConfig
import coffee.cypher.hexbound.interop.InteropManager
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import org.quiltmc.loader.api.ModContainer
import org.quiltmc.loader.api.QuiltLoader
import org.quiltmc.qkl.library.brigadier.argument.double
import org.quiltmc.qkl.library.brigadier.argument.literal
import org.quiltmc.qkl.library.brigadier.argument.value
import org.quiltmc.qkl.library.brigadier.execute
import org.quiltmc.qkl.library.brigadier.register
import org.quiltmc.qkl.library.brigadier.required
import org.quiltmc.qkl.library.brigadier.util.player
import org.quiltmc.qkl.library.brigadier.util.required
import org.quiltmc.qkl.library.brigadier.util.sendFeedback
import org.quiltmc.qkl.library.brigadier.util.world
import org.quiltmc.qkl.library.commands.onCommandRegistration
import org.quiltmc.qkl.library.registerEvents
import org.quiltmc.qkl.library.text.buildText
import org.quiltmc.qkl.library.text.literal
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Hexbound : ModInitializer {
    const val MOD_ID = "hexbound"

    val LOGGER: Logger by lazy {
        LoggerFactory.getLogger(MOD_ID)
    }

    fun id(name: String): Identifier {
        return Identifier(MOD_ID, name)
    }

    override fun onInitialize(mod: ModContainer) {

        HexboundConfig.init()

        HexboundData.init()
        HexboundPatterns.register()
        InteropManager.init()

        if (QuiltLoader.isDevelopmentEnvironment()) {
            enableDebugFeatures()
        }
    }

    private fun enableDebugFeatures() {
        registerEvents {
            onCommandRegistration { _, _ ->
                register("hexbound") {
                    required(literal("getConstructCommands")) {
                        execute {
                            sendFeedback(buildText {
                                HexboundData.Registries.CONSTRUCT_COMMANDS.ids.forEach {
                                    literal("[$it -> ${HexboundData.Registries.CONSTRUCT_COMMANDS.get(it)}]\n")
                                }
                            })
                        }
                    }

                    required(literal("uncraft")) {
                        execute {
                            val stack = player!!.getStackInHand(Hand.MAIN_HAND)

                            @Suppress("OverrideOnly")
                            val hex = (stack.item as HexHolderItem).getHex(stack, world)!!
                            player!!.setStackInHand(
                                Hand.MAIN_HAND,
                                HexItems.FOCUS.defaultStack.also { HexItems.FOCUS.writeDatum(it, ListIota(hex)) })
                        }
                    }

                    required(literal("facingVector"), double("pitch"), double("yaw")) { _, pitch, yaw ->
                        execute {
                            sendFeedback(
                                Text.literal(
                                    Vec3d.fromPolar(
                                        pitch().value().toFloat(),
                                        yaw().value().toFloat()
                                    ).toString()
                                )
                            )
                        }
                    }

                    required(literal("funny_focus")) {
                        execute {
                            player?.setStackInHand(
                                Hand.MAIN_HAND,
                                ItemStack(HexItems.FOCUS).also {
                                    HexItems.FOCUS.writeDatum(
                                        it,
                                        ItemStackIota.createFiltered(
                                            ItemStack(Items.ENDER_PEARL, 412312312)
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
