package coffee.cypher.hexbound.init

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import coffee.cypher.hexbound.init.Hexbound.id
import coffee.cypher.hexbound.feature.pattern_editing.action.OpMergePatterns
import coffee.cypher.hexbound.feature.pattern_editing.action.OpRotatePattern
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerDelete
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerLoad
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerSave
import coffee.cypher.hexbound.feature.construct.action.*
import coffee.cypher.hexbound.feature.fake_circles.action.OpSetImpetusFakePlayer
import coffee.cypher.hexbound.feature.item_patterns.action.*
import net.minecraft.util.Hand

object HexboundPatterns {
    fun register() {
        registerItemPatterns()
        registerPatternManipulation()
        registerMemorizedColorizers()

        registerMinionPatterns()

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qaqdaqwqaeedewd", HexDir.NORTH_EAST),
            id("set_fake_impetus_player"),
            OpSetImpetusFakePlayer,
            true
        )
    }

    private fun registerItemPatterns() {
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

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("aqwed", HexDir.NORTH_EAST),
            id("get_inventory/stacks"),
            OpGetInventoryContents(returnStacks = true)
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("dewqa", HexDir.NORTH_EAST),
            id("get_inventory/items"),
            OpGetInventoryContents(returnStacks = false)
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wqaqwaq", HexDir.EAST),
            id("get_entity_item"),
            OpGetEntityItem
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("dedqaa", HexDir.WEST),
            id("get_stack_prop/item"),
            OpGetStackItem
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("dedqaq", HexDir.WEST),
            id("get_stack_prop/size"),
            OpGetStackSize
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
            HexPattern.fromAngles("qaawedee", HexDir.EAST),
            id("construct_get_self"),
            OpConstructGetSelf
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("eddeawaw", HexDir.EAST),
            id("give_command/pick_up"),
            OpGiveCommandPickUp
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qaaqdwdw", HexDir.EAST),
            id("give_command/drop_off"),
            OpGiveCommandDropOff
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qaaqwdaqqqa", HexDir.EAST),
            id("give_command/move_to"),
            OpGiveCommandMoveTo
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qaaqqedwed", HexDir.EAST),
            id("give_command/harvest"),
            OpGiveCommandHarvest
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qaaqdee", HexDir.EAST),
            id("give_command/use/block"),
            OpGiveCommandUseOnBlock
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qaaq", HexDir.EAST),
            id("send_instructions"),
            OpSendInstructions
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("qqaaqqqqwq", HexDir.SOUTH_EAST),
            id("broadcast_instructions"),
            OpBroadcastInstructions
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wqaawddewdwewewewewew", HexDir.EAST),
            id("bind_construct"),
            OpBindConstruct
        )

        PatternRegistry.mapPattern(
            HexPattern.fromAngles("wqwqwwqwqwqwwaeqaqdwdqaqe", HexDir.SOUTH_WEST),
            id("create_construct/spider"),
            OpCreateSpiderConstruct
        )
    }
}
