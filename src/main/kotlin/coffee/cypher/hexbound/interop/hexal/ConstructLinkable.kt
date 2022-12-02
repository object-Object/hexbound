package coffee.cypher.hexbound.interop.hexal

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.Action
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.utils.asCompound
import at.petrak.hexcasting.api.utils.asInt
import at.petrak.hexcasting.api.utils.asList
import at.petrak.hexcasting.api.utils.asUUID
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.init.Hexbound
import coffee.cypher.hexbound.util.mixinaccessor.construct
import net.minecraft.nbt.*
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.quiltmc.qkl.library.nbt.set
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.api.linkable.LinkableRegistry
import ram.talia.hexal.api.nbt.SerialisedIotaList
import ram.talia.hexal.client.playLinkParticles

class ConstructLinkable constructor(val construct: AbstractConstructEntity) : ILinkable<ConstructLinkable>,
    ILinkable.IRenderCentre {
    override val _lazyLinked: ILinkable.LazyILinkableList? = if (construct.world.isClient)
        null
    else
        ILinkable.LazyILinkableList(construct.world as ServerWorld)

    override val _lazyRenderLinks = if (construct.world.isClient)
        null
    else
        ILinkable.LazyILinkableList(construct.world as ServerWorld)

    override val _level: World = construct.world
    override val _serReceivedIotas = SerialisedIotaList(null)

    override val asActionResult = listOf(EntityIota(construct))

    override fun get(): ConstructLinkable {
        return this
    }

    override fun colouriser(): FrozenColorizer {
        return FrozenColorizer.DEFAULT.get()
    }

    override fun getLinkableType(): LinkableRegistry.LinkableType<ConstructLinkable, *> {
        return ConstructLinkableType
    }

    override fun renderCentre(other: ILinkable.IRenderCentre, recursioning: Boolean): Vec3d {
        return construct.boundingBox.center
    }

    override fun getPos(): Vec3d {
        return construct.pos
    }

    override fun maxSqrLinkRange(): Double {
        return Action.MAX_DISTANCE * Action.MAX_DISTANCE
    }

    override fun shouldRemove(): Boolean {
        return construct.isRemoved && construct.removalReason?.shouldDestroy() == true
    }

    private fun syncRenderLinks() {
        if (construct.world.isClient)
            throw IllegalStateException("LinkableEntity.syncRenderLinks should only be accessed on server.")

        val compound = NbtCompound()
        compound["render_links"] = _lazyRenderLinks!!.get().mapTo(NbtList()) { LinkableRegistry.wrapSync(it) }
        construct.hexalLinks = compound
    }

    override fun syncAddRenderLink(other: ILinkable<*>) = syncRenderLinks()

    override fun syncRemoveRenderLink(other: ILinkable<*>) = syncRenderLinks()

    override fun writeToNbt(): NbtElement {
        return NbtHelper.fromUuid(construct.uuid)
    }

    override fun writeToSync(): NbtElement {
        return NbtInt.of(construct.id)
    }

    companion object {
        fun handleLinkRendering(construct: AbstractConstructEntity) {
            if (construct.world.isClient) {
                val linkable = construct.getLinkable()

                construct.hexalLinks["render_links"]?.asList?.mapNotNull {
                    LinkableRegistry.fromSync(
                        it.asCompound,
                        construct.world
                    )
                }?.forEach { playLinkParticles(linkable, it, construct.random, construct.world) }
            }
        }
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

fun AbstractConstructEntity.getLinkable(): ConstructLinkable {
    if (hexalLinkable == null) {
        hexalLinkable = ConstructLinkable(this)
    }

    return hexalLinkable as ConstructLinkable
}
