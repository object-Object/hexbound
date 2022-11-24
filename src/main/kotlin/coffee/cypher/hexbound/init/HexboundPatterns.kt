package coffee.cypher.hexbound.init

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import coffee.cypher.hexbound.init.Hexbound.id
import coffee.cypher.hexbound.operator.OpGetHeldItem
import coffee.cypher.hexbound.operator.OpMergePatterns
import coffee.cypher.hexbound.operator.OpRotatePattern
import coffee.cypher.hexbound.feature.colorizerstorage.casting.OpColorizerDelete
import coffee.cypher.hexbound.feature.colorizerstorage.casting.OpColorizerLoad
import coffee.cypher.hexbound.feature.colorizerstorage.casting.OpColorizerSave
import coffee.cypher.hexbound.operator.great.OpFakeImprint
import coffee.cypher.hexbound.feature.construct.casting.OpGiveCommandDropOff
import coffee.cypher.hexbound.feature.construct.casting.OpGiveCommandMoveTo
import coffee.cypher.hexbound.feature.construct.casting.OpGiveCommandPickUp
import coffee.cypher.hexbound.feature.construct.casting.OpSendInstructions
import net.minecraft.util.Hand

object HexboundPatterns {
    fun register() {
        registerHandAccess()
        registerPatternManipulation()
        registerMemorizedColorizers()

        registerMinionPatterns()

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qqaq", HexDir.EAST),
            id("set_fake_impetus_player"),
            OpFakeImprint
        )
    }

    private fun registerHandAccess() {
        PatternRegistry.mapPattern(
            HexPattern.fromAngles("adeq", HexDir.EAST),
            id("get_main_hand"),
            OpGetHeldItem(Hand.MAIN_HAND)
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qeda", HexDir.EAST),
            id("get_off_hand"),
            OpGetHeldItem(Hand.OFF_HAND)
        )
    }

    private fun registerPatternManipulation() {
        PatternRegistry.mapPattern(
            HexPattern.fromAngles("deeee", HexDir.WEST),
            id("rotate_pattern"),
            OpRotatePattern
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("aqqqqa", HexDir.NORTH_WEST),
            id("merge_patterns"),
            OpMergePatterns
        )
    }

    private fun registerMemorizedColorizers() {
        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wqwawqqawddwqwede", HexDir.NORTH_EAST),
            id("save_colorizer"),
            OpColorizerSave
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wqwawqqawddwqeqaq", HexDir.NORTH_EAST),
            id("load_colorizer"),
            OpColorizerLoad
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wqwawqqawddwqwdd", HexDir.NORTH_EAST),
            id("delete_colorizer"),
            OpColorizerDelete
        )
    }

    private fun registerMinionPatterns() {
        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wwed", HexDir.NORTH_EAST),
            id("give_command_pick_up"),
            OpGiveCommandPickUp
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wwedw", HexDir.NORTH_EAST),
            id("give_command_drop_off"),
            OpGiveCommandDropOff
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wwedww", HexDir.NORTH_EAST),
            id("give_command_move_to"),
            OpGiveCommandMoveTo
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wwedwww", HexDir.NORTH_EAST),
            id("send_instructions"),
            OpSendInstructions
        )
    }
}
