package coffee.cypher.hexbound.feature.construct.action

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.evaluatable
import at.petrak.hexcasting.api.spell.getItemEntity
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import coffee.cypher.hexbound.feature.construct.command.ConstructCommand
import coffee.cypher.hexbound.feature.construct.command.DropOff
import coffee.cypher.hexbound.feature.construct.command.MoveTo
import coffee.cypher.hexbound.feature.construct.command.PickUp
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.construct.entity.component.ConstructComponentKey
import coffee.cypher.hexbound.feature.construct.entity.component.ItemHolderComponent
import coffee.cypher.hexbound.feature.construct.mishap.MishapMissingConstructComponent
import coffee.cypher.hexbound.feature.construct.mishap.MishapNoConstruct
import coffee.cypher.hexbound.util.mixinaccessor.construct

abstract class OpGiveCommand : ConstMediaAction {
    override val argc: Int
        get() = baseArgc + 1

    protected abstract val baseArgc: Int

    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val construct = ctx.construct ?: throw MishapNoConstruct()
        val callback = evaluatable(args.last(), 0).map({ listOf(PatternIota(it)) }, { it.toList() })

        val command = getCommand(
            args,
            ctx,
            construct
        )

        ctx.incDepth()
        construct.executeCommand(command, callback, ctx.world)

        return emptyList()
    }

    protected fun <T : Any> AbstractConstructEntity.requireComponent(key: ConstructComponentKey<T>): T {
        return getComponent(key) ?: throw MishapMissingConstructComponent(key)
    }

    protected abstract fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*>
}

object OpGiveCommandPickUp : OpGiveCommand() {
    override val baseArgc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        val target = args.getItemEntity(0, argc)
        constructEntity.requireComponent(ItemHolderComponent)

        return PickUp(target.uuid)
    }
}

object OpGiveCommandDropOff : OpGiveCommand() {
    override val baseArgc = 0

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        constructEntity.requireComponent(ItemHolderComponent)

        return DropOff()
    }
}

object OpGiveCommandMoveTo : OpGiveCommand() {
    override val baseArgc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: AbstractConstructEntity
    ): ConstructCommand<*> {
        val pos = args.getVec3(0, argc)

        return MoveTo(pos)
    }
}
