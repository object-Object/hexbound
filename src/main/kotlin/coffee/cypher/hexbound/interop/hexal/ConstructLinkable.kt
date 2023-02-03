package coffee.cypher.hexbound.interop.hexal

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.asInt
import at.petrak.hexcasting.api.utils.asUUID
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.util.mixinaccessor.construct
import dev.onyxstudios.cca.api.v3.component.ComponentV3
import dev.onyxstudios.cca.api.v3.component.tick.ClientTickingComponent
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtHelper
import net.minecraft.nbt.NbtInt
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import ram.talia.hexal.api.linkable.ClientLinkableHolder
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.api.linkable.LinkableRegistry
import ram.talia.hexal.api.linkable.ServerLinkableHolder

class ConstructLinkable(val construct: AbstractConstructEntity) : ILinkable,
    ILinkable.IRenderCentre,
    ClientTickingComponent,
    ComponentV3 {
    override val asActionResult = listOf(EntityIota(construct))

    override val linkableHolder = if (!construct.world.isClient)
        ServerLinkableHolder(this, construct.world as ServerWorld)
    else
        null

    override val clientLinkableHolder = if (construct.world.isClient)
        ClientLinkableHolder(this, construct.world, construct.random)
    else
        null

    override fun getLinkableType(): LinkableRegistry.LinkableType<ConstructLinkable, *> {
        return ConstructLinkableType
    }

    override fun getPosition(): Vec3d {
        return construct.pos
    }

    override fun maxSqrLinkRange(): Double {
        return Action.MAX_DISTANCE * Action.MAX_DISTANCE
    }

    override fun shouldRemove(): Boolean {
        return construct.isRemoved && construct.removalReason?.shouldDestroy() == true
    }

    override fun writeToNbt(): NbtElement {
        return NbtHelper.fromUuid(construct.uuid)
    }

    override fun writeToSync(): NbtElement {
        return NbtInt.of(construct.id)
    }

    override fun colouriser(): FrozenColorizer {
        return FrozenColorizer.DEFAULT.get()
    }

    override fun renderCentre(other: ILinkable.IRenderCentre, recursioning: Boolean): Vec3d {
        return construct.boundingBox.center
    }

    override fun readFromNbt(tag: NbtCompound) {
        if (linkableHolder != null) {
            if (tag.contains("linkable_holder")) {
                linkableHolder.readFromNbt(tag.getCompound("linkable_holder"))
            }
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        if (linkableHolder != null) {
            tag.put("linkable_holder", linkableHolder.writeToNbt())
        }
    }

    override fun clientTick() {
        clientLinkableHolder?.renderLinks()
        checkLinks()
    }
}

object ConstructLinkableType :
    LinkableRegistry.LinkableType<ConstructLinkable, ConstructLinkable>(Hexbound.id("linkable_construct")) {
    override val canCast = true
    override val castingContextPriority = 0
    override val iotaPriority = 0

    override fun fromNbt(tag: NbtElement, level: ServerWorld): ConstructLinkable? {
        return (level.getEntity(tag.asUUID) as? AbstractConstructEntity)?.getLinkable()
    }

    override fun fromSync(tag: NbtElement, level: World): ConstructLinkable? {
        return (level.getEntityById(tag.asInt) as? AbstractConstructEntity)?.getLinkable()
    }

    override fun linkableFromCastingContext(ctx: CastingContext): ConstructLinkable? {
        return ctx.construct?.getLinkable()
    }

    override fun linkableFromIota(iota: Iota): ConstructLinkable? {
        return ((iota as? EntityIota)?.entity as? AbstractConstructEntity)?.getLinkable()
    }

    override fun matchSync(centre: ILinkable.IRenderCentre, tag: NbtElement): Boolean {
        return (centre as? ConstructLinkable)?.construct?.id == tag.asInt
    }
}
