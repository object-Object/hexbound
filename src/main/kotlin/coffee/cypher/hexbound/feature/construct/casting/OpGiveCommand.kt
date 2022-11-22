package coffee.cypher.hexbound.feature.construct.casting

import at.petrak.hexcasting.api.spell.ConstMediaAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.getItemEntity
import at.petrak.hexcasting.api.spell.getVec3
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.mixinaccessor.construct

abstract class OpGiveCommand : ConstMediaAction {
    override fun execute(args: List<Iota>, ctx: CastingContext): List<Iota> {
        val construct = ctx.construct ?: throw IllegalStateException() //TODO mishap

        val command = getCommand(args, ctx, construct as SpiderConstructEntity)

        ctx.construct!!.executeCommand(command)

        return emptyList()
    }

    protected abstract fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: SpiderConstructEntity
    ): SpiderConstructEntity.Command
}

object OpGiveCommandPickUp : OpGiveCommand() {
    override val argc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: SpiderConstructEntity
    ): SpiderConstructEntity.Command {
        val target = args.getItemEntity(0, argc)

        if (ctx.construct!!.pos.distanceTo(target.pos) > 3) {
            throw IllegalStateException() // TODO mishap also consider whether things should mishap on impossible commands like too far or no item
        }

        return SpiderConstructEntity.Command.PickUp(target, constructEntity)
    }
}

object OpGiveCommandDropOff : OpGiveCommand() {
    override val argc = 0

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: SpiderConstructEntity
    ): SpiderConstructEntity.Command {
        //TODO mishap if no item?
        return SpiderConstructEntity.Command.DropOff(constructEntity)
    }
}

object OpGiveCommandMoveTo : OpGiveCommand() {
    override val argc = 1

    override fun getCommand(
        args: List<Iota>,
        ctx: CastingContext,
        constructEntity: SpiderConstructEntity
    ): SpiderConstructEntity.Command {
        val pos = args.getVec3(0, argc)

        //TODO too far mishap

        return SpiderConstructEntity.Command.MoveTo(pos, constructEntity)
    }
}
