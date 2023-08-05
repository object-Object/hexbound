package coffee.cypher.hexbound.init

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntitiesBy
import at.petrak.hexcasting.common.casting.actions.selectors.OpGetEntityAt
import at.petrak.hexcasting.common.lib.hex.HexActions
import coffee.cypher.hexbound.feature.pigment_storage.action.OpPigmentDelete
import coffee.cypher.hexbound.feature.pigment_storage.action.OpPigmentLoad
import coffee.cypher.hexbound.feature.pigment_storage.action.OpPigmentSave
import coffee.cypher.hexbound.feature.combat.shield.OpCreateShield
import coffee.cypher.hexbound.feature.combat.shield.ShieldEntity
import coffee.cypher.hexbound.feature.construct.casting.action.OpBindConstruct
import coffee.cypher.hexbound.feature.construct.casting.action.OpConstructGetSelf
import coffee.cypher.hexbound.feature.construct.casting.action.command.*
import coffee.cypher.hexbound.feature.construct.casting.action.crafting.OpCreateSpiderConstruct
import coffee.cypher.hexbound.feature.construct.casting.action.instruction.OpBroadcastInstructions
import coffee.cypher.hexbound.feature.construct.casting.action.instruction.OpSendInstructions
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.pattern_editing.action.*
import coffee.cypher.hexbound.interop.RootInteropManager
import net.minecraft.entity.Entity
import org.quiltmc.qkl.library.registry.withId

open class HexboundActions {
    companion object Default : HexboundActions()

    protected open fun registerAction(
        pattern: HexPattern,
        id: String,
        action: Action,
        perWorld: Boolean = false
    ) {
        ActionRegistryEntry(pattern, action) withId Hexbound.id(id) toRegistry HexActions.REGISTRY
    }

    fun register() {
        registerPatternManipulation()
        registerMemorizedPigments()
        registerCombatPatterns()
        registerConstructPatterns()

        RootInteropManager.registerActions(this::registerAction)
    }

    private fun registerCombatPatterns() {
        registerAction(
            HexPattern.fromAngles("eweeewe", HexDir.NORTH_EAST),
            "create_shield/normal",
            OpCreateShield(ShieldEntity.VisualType.REGULAR)
        )

        registerAction(
            HexPattern.fromAngles("eqdweeqdw", HexDir.NORTH_EAST),
            "create_shield/glitchy",
            OpCreateShield(ShieldEntity.VisualType.GLITCHY)
        )
    }

    private fun registerPatternManipulation() {
        registerAction(
            HexPattern.fromAngles("deeee", HexDir.WEST),
            "pattern/rotate",
            OpRotatePattern
        )

        registerAction(
            HexPattern.fromAngles("aqqqqa", HexDir.NORTH_WEST),
            "pattern/merge",
            OpMergePatterns
        )

        registerAction(
            HexPattern.fromAngles("wqqqq", HexDir.EAST),
            "pattern/head",
            OpPatternHead
        )

        registerAction(
            HexPattern.fromAngles("weeee", HexDir.WEST),
            "pattern/tail",
            OpPatternTail
        )

        registerAction(
            HexPattern.fromAngles("qeeee", HexDir.WEST),
            "pattern/start_dir",
            OpPatternStartDir
        )

        registerAction(
            HexPattern.fromAngles("eqqqq", HexDir.WEST),
            "pattern/line_count",
            OpPatternLineCount
        )
    }

    private fun registerMemorizedPigments() {
        registerAction(
            HexPattern.fromAngles("wqwawqqawddwqwede", HexDir.NORTH_EAST),
            "pigment/save",
            OpPigmentSave
        )

        registerAction(
            HexPattern.fromAngles("wqwawqqawddwqeqaq", HexDir.NORTH_EAST),
            "pigment/load",
            OpPigmentLoad
        )

        registerAction(
            HexPattern.fromAngles("wqwawqqawddwqwdd", HexDir.NORTH_EAST),
            "pigment/delete",
            OpPigmentDelete
        )
    }

    private fun registerConstructPatterns() {
        registerAction(
            HexPattern.fromAngles("qaawedee", HexDir.EAST),
            "construct_get_self",
            OpConstructGetSelf
        )

        registerAction(
            HexPattern.fromAngles("eddeawaw", HexDir.EAST),
            "give_command/pick_up",
            OpGiveCommandPickUp
        )

        registerAction(
            HexPattern.fromAngles("qaaqdwdw", HexDir.EAST),
            "give_command/drop_off",
            OpGiveCommandDropOff
        )

        registerAction(
            HexPattern.fromAngles("qaaqwdaqqqa", HexDir.EAST),
            "give_command/move_to",
            OpGiveCommandMoveTo
        )

        registerAction(
            HexPattern.fromAngles("qaaqqedwed", HexDir.EAST),
            "give_command/harvest",
            OpGiveCommandHarvest
        )

        registerAction(
            HexPattern.fromAngles("qaaqdee", HexDir.EAST),
            "give_command/use/block",
            OpGiveCommandUseOnBlock
        )

        registerAction(
            HexPattern.fromAngles("qaaq", HexDir.EAST),
            "instructions/send",
            OpSendInstructions
        )

        registerAction(
            HexPattern.fromAngles("qqaaqqqqwq", HexDir.SOUTH_EAST),
            "instructions/broadcast",
            OpBroadcastInstructions
        )

        registerAction(
            HexPattern.fromAngles("wqaawddewdwewewewewew", HexDir.EAST),
            "bind_construct",
            OpBindConstruct
        )

        registerAction(
            HexPattern.fromAngles("wqwqwwqwqwqwwaeqaqdwdqaqe", HexDir.SOUTH_WEST),
            "create_construct/spider",
            OpCreateSpiderConstruct
        )

        val constructPredicate = { e: Entity -> e is AbstractConstructEntity }
        registerAction(
            HexPattern.fromAngles("qqqqqdaqaawedde", HexDir.SOUTH_EAST),
            "get_entity/construct",
            OpGetEntityAt(constructPredicate)
        )

        registerAction(
            HexPattern.fromAngles("qqqqqwdeddwedde", HexDir.SOUTH_EAST),
            "zone_entity/construct",
            OpGetEntitiesBy(constructPredicate, false)
        )

        registerAction(
            HexPattern.fromAngles("eeeeewaqaawedde", HexDir.SOUTH_EAST),
            "zone_entity/not_construct",
            OpGetEntitiesBy(constructPredicate, true)
        )
    }
}
