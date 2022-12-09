package coffee.cypher.hexbound.init

import at.petrak.hexcasting.api.PatternRegistry
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.math.HexDir
import at.petrak.hexcasting.api.spell.math.HexPattern
import coffee.cypher.hexbound.feature.pattern_editing.action.OpMergePatterns
import coffee.cypher.hexbound.feature.pattern_editing.action.OpRotatePattern
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerDelete
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerLoad
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerSave
import coffee.cypher.hexbound.feature.construct.action.*
import coffee.cypher.hexbound.feature.construct.action.command.*
import coffee.cypher.hexbound.feature.construct.action.crafting.OpCreateSpiderConstruct
import coffee.cypher.hexbound.feature.construct.action.instruction.OpBroadcastInstructions
import coffee.cypher.hexbound.feature.construct.action.instruction.OpSendInstructions
import coffee.cypher.hexbound.feature.fake_circles.action.OpSetImpetusFakePlayer
import coffee.cypher.hexbound.feature.item_patterns.action.*
import net.minecraft.util.Hand

open class HexboundPatterns {
    companion object Default : HexboundPatterns()

    protected open fun registerPattern(
        pattern: HexPattern,
        id: String,
        action: Action,
        perWorld: Boolean = false
    ) {
        PatternRegistry.mapPattern(
            pattern,
            Hexbound.id(id),
            action,
            perWorld
        )
    }

    fun register() {
        registerItemPatterns()
        registerPatternManipulation()
        registerMemorizedColorizers()

        registerMinionPatterns()

        registerPattern(
            HexPattern.fromAngles("qaqdaqwqaeedewd", HexDir.NORTH_EAST),
            "set_fake_impetus_player",
            OpSetImpetusFakePlayer,
            true
        )
    }

    private fun registerItemPatterns() {
        registerPattern(
            HexPattern.fromAngles("adeq", HexDir.EAST),
            "get_main_hand",
            OpGetHeldItem(Hand.MAIN_HAND)
        )

        registerPattern(
            HexPattern.fromAngles("qeda", HexDir.EAST),
            "get_off_hand",
            OpGetHeldItem(Hand.OFF_HAND)
        )

        registerPattern(
            HexPattern.fromAngles("aqwed", HexDir.NORTH_EAST),
            "get_inventory/stacks",
            OpGetInventoryContents(returnStacks = true)
        )

        registerPattern(
            HexPattern.fromAngles("dewqa", HexDir.NORTH_EAST),
            "get_inventory/items",
            OpGetInventoryContents(returnStacks = false)
        )

        registerPattern(
            HexPattern.fromAngles("wqaqwaq", HexDir.EAST),
            "get_entity_item",
            OpGetEntityItem
        )

        registerPattern(
            HexPattern.fromAngles("dedqaa", HexDir.WEST),
            "get_stack_prop/item",
            OpGetStackItem
        )

        registerPattern(
            HexPattern.fromAngles("dedqaq", HexDir.WEST),
            "get_stack_prop/size",
            OpGetStackSize
        )
    }

    private fun registerPatternManipulation() {
        registerPattern(
            HexPattern.fromAngles("deeee", HexDir.WEST),
            "rotate_pattern",
            OpRotatePattern
        )

        registerPattern(
            HexPattern.fromAngles("aqqqqa", HexDir.NORTH_WEST),
            "merge_patterns",
            OpMergePatterns
        )
    }

    private fun registerMemorizedColorizers() {
        registerPattern(
            HexPattern.fromAngles("wqwawqqawddwqwede", HexDir.NORTH_EAST),
            "colorizer/save",
            OpColorizerSave
        )

        registerPattern(
            HexPattern.fromAngles("wqwawqqawddwqeqaq", HexDir.NORTH_EAST),
            "colorizer/load",
            OpColorizerLoad
        )

        registerPattern(
            HexPattern.fromAngles("wqwawqqawddwqwdd", HexDir.NORTH_EAST),
            "colorizer/delete",
            OpColorizerDelete
        )
    }

    private fun registerMinionPatterns() {
        registerPattern(
            HexPattern.fromAngles("qaawedee", HexDir.EAST),
            "construct_get_self",
            OpConstructGetSelf
        )

        registerPattern(
            HexPattern.fromAngles("eddeawaw", HexDir.EAST),
            "give_command/pick_up",
            OpGiveCommandPickUp
        )

        registerPattern(
            HexPattern.fromAngles("qaaqdwdw", HexDir.EAST),
            "give_command/drop_off",
            OpGiveCommandDropOff
        )

        registerPattern(
            HexPattern.fromAngles("qaaqwdaqqqa", HexDir.EAST),
            "give_command/move_to",
            OpGiveCommandMoveTo
        )

        registerPattern(
            HexPattern.fromAngles("qaaqqedwed", HexDir.EAST),
            "give_command/harvest",
            OpGiveCommandHarvest
        )

        registerPattern(
            HexPattern.fromAngles("qaaqdee", HexDir.EAST),
            "give_command/use/block",
            OpGiveCommandUseOnBlock
        )

        registerPattern(
            HexPattern.fromAngles("qaaq", HexDir.EAST),
            "instructions/send",
            OpSendInstructions
        )

        registerPattern(
            HexPattern.fromAngles("qqaaqqqqwq", HexDir.SOUTH_EAST),
            "instructions/broadcast",
            OpBroadcastInstructions
        )

        registerPattern(
            HexPattern.fromAngles("wqaawddewdwewewewewew", HexDir.EAST),
            "bind_construct",
            OpBindConstruct
        )

        registerPattern(
            HexPattern.fromAngles("wqwqwwqwqwqwwaeqaqdwdqaqe", HexDir.SOUTH_WEST),
            "create_construct/spider",
            OpCreateSpiderConstruct
        )
    }
}
