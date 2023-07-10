package coffee.cypher.hexbound.init

import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntitiesBy
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntityAt
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerDelete
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerLoad
import coffee.cypher.hexbound.feature.colorizer_storage.action.OpColorizerSave
import coffee.cypher.hexbound.feature.combat.shield.OpCreateShield
import coffee.cypher.hexbound.feature.combat.shield.ShieldEntity
import coffee.cypher.hexbound.feature.construct.action.*
import coffee.cypher.hexbound.feature.construct.action.command.*
import coffee.cypher.hexbound.feature.construct.casting.action.crafting.OpCreateSpiderConstruct
import coffee.cypher.hexbound.feature.construct.casting.action.instruction.OpBroadcastInstructions
import coffee.cypher.hexbound.feature.construct.casting.action.instruction.OpSendInstructions
import coffee.cypher.hexbound.feature.construct.casting.action.OpBindConstruct
import coffee.cypher.hexbound.feature.construct.casting.action.OpConstructGetSelf
import coffee.cypher.hexbound.feature.construct.casting.action.command.*
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.fake_circles.action.OpSetImpetusFakePlayer
import coffee.cypher.hexbound.feature.item_patterns.action.*
import coffee.cypher.hexbound.feature.pattern_editing.action.*
import net.minecraft.entity.Entity
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

        registerCombatPatterns()

        registerConstructPatterns()

        registerPattern(
            HexPattern.fromAngles("qaqdaqwqaeedewd", HexDir.NORTH_EAST),
            "set_fake_impetus_player",
            OpSetImpetusFakePlayer,
            true
        )
    }

    private fun registerCombatPatterns() {
        registerPattern(
            HexPattern.fromAngles("eweeewe", HexDir.NORTH_EAST),
            "create_shield/normal",
            OpCreateShield(ShieldEntity.VisualType.REGULAR)
        )

        registerPattern(
            HexPattern.fromAngles("eqdweeqdw", HexDir.NORTH_EAST),
            "create_shield/glitchy",
            OpCreateShield(ShieldEntity.VisualType.GLITCHY)
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
            "pattern/rotate",
            OpRotatePattern
        )

        registerPattern(
            HexPattern.fromAngles("aqqqqa", HexDir.NORTH_WEST),
            "pattern/merge",
            OpMergePatterns
        )

        registerPattern(
            HexPattern.fromAngles("wqqqq", HexDir.EAST),
            "pattern/head",
            OpPatternHead
        )

        registerPattern(
            HexPattern.fromAngles("weeee", HexDir.WEST),
            "pattern/tail",
            OpPatternTail
        )

        registerPattern(
            HexPattern.fromAngles("qeeee", HexDir.WEST),
            "pattern/start_dir",
            OpPatternStartDir
        )

        registerPattern(
            HexPattern.fromAngles("eqqqq", HexDir.WEST),
            "pattern/line_count",
            OpPatternLineCount
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

    private fun registerConstructPatterns() {
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

        val constructPredicate = { e: Entity -> e is AbstractConstructEntity }
        registerPattern(
            HexPattern.fromAngles("qqqqqdaqaawedde", HexDir.SOUTH_EAST),
            "get_entity/construct",
            OpGetEntityAt(constructPredicate)
        )

        registerPattern(
            HexPattern.fromAngles("qqqqqwdeddwedde", HexDir.SOUTH_EAST),
            "zone_entity/construct",
            OpGetEntitiesBy(constructPredicate, false)
        )

        registerPattern(
            HexPattern.fromAngles("eeeeewaqaawedde", HexDir.SOUTH_EAST),
            "zone_entity/not_construct",
            OpGetEntitiesBy(constructPredicate, true)
        )
    }
}
