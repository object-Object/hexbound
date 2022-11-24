package coffee.cypher.hexbound.feature.construct.casting

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getItemEntity
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.construct.command.ConstructCommand
import coffee.cypher.hexbound.feature.construct.command.DropOff
import coffee.cypher.hexbound.feature.construct.command.MoveTo
import coffee.cypher.hexbound.feature.construct.command.PickUp
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.mixinaccessor.construct

abstract class OpGiveCommand<T : AbstractConstructEntity<*>> : ConstMediaAction {
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val construct = ctx.construct ?: throw IllegalStateException() //TODO mishap
        val validated = validateConstruct(construct)

        @Suppress("UNCHECKED_CAST")
        val command = getCommand(
            args,
            ctx,
            validated
        ) as ConstructCommand<AbstractConstructEntity<*>, *>

        validated.executeCommand(command, ctx.world)

        return emptyList()
    }

    protected abstract fun validateConstruct(construct: AbstractConstructEntity<*>): T

    protected abstract fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: T
    ): ConstructCommand<T, *>
}

abstract class OpGiveSpiderCommand : OpGiveCommand<SpiderConstructEntity>() {
    override fun validateConstruct(construct: AbstractConstructEntity<*>): SpiderConstructEntity {
        return construct as? SpiderConstructEntity ?: throw IllegalStateException() //TODO mishap
    }
}

abstract class OpGiveGenericCommand : OpGiveCommand<AbstractConstructEntity<*>>() {
    override fun validateConstruct(construct: AbstractConstructEntity<*>): AbstractConstructEntity<*> {
        return construct
    }
}

object OpGiveCommandPickUp : OpGiveSpiderCommand() {
    override val argc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: SpiderConstructEntity
    ): ConstructCommand<SpiderConstructEntity, *> {
        val target = args.getItemEntity(0, argc)

        if (ctx.construct!!.pos.distanceTo(target.pos) > 3) {
            throw IllegalStateException() // TODO mishap also consider whether things should mishap on impossible commands like too far or no item
        }

        return PickUp(target.uuid)
    }
}

object OpGiveCommandDropOff : OpGiveSpiderCommand() {
    override val argc = 0

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: SpiderConstructEntity
    ): ConstructCommand<SpiderConstructEntity, *> {
        //TODO mishap if no item?
        return DropOff()
    }
}

object OpGiveCommandMoveTo : OpGiveGenericCommand() {
    override val argc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: AbstractConstructEntity<*>
    ): ConstructCommand<AbstractConstructEntity<*>, *> {
        val pos = args.getVec3(0, argc)

        //TODO too far mishap

        return MoveTo(pos)
    }
}
