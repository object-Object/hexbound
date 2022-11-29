package coffee.cypher.hexbound.interop.hexal

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.init.Hexbound
import net.minecraft.nbt.NbtElement
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import ram.talia.hexal.api.linkable.ILinkable
import ram.talia.hexal.api.linkable.LinkableRegistry

class ConstructLinkable(private val construct: AbstractConstructEntity) : ILinkable<ConstructLinkable> {
    override val asActionResult: List<Iota> = listOf(EntityIota(construct))

    override fun get(): ConstructLinkable {
        return this
    }

    override fun getLinkableType(): LinkableRegistry.LinkableType<ConstructLinkable, *> {
        TODO("Not yet implemented")
    }

    override fun getLinked(index: Int): ILinkable<*> {
        TODO("Not yet implemented")
    }

    override fun getLinkedIndex(linked: ILinkable<*>): Int {
        TODO("Not yet implemented")
    }

    override fun getPos(): Vec3d {
        return construct.pos
    }

    override fun link(other: ILinkable<*>, linkOther: Boolean) {
        TODO("Not yet implemented")
    }

    override fun maxSqrLinkRange(): Double {
        TODO("Not yet implemented")
    }

    override fun nextReceivedIota(): Iota {
        TODO("Not yet implemented")
    }

    override fun numLinked(): Int {
        TODO("Not yet implemented")
    }

    override fun numRemainingIota(): Int {
        TODO("Not yet implemented")
    }

    override fun receiveIota(iota: Iota) {
        TODO("Not yet implemented")
    }

    override fun shouldRemove(): Boolean {
        return construct.isRemoved
    }

    override fun unlink(other: ILinkable<*>, unlinkOther: Boolean) {
        TODO("Not yet implemented")
    }

    override fun writeToNbt(): NbtElement {
        TODO("Not yet implemented")
    }

    override fun writeToSync(): NbtElement {
        TODO("Not yet implemented")
    }

    object Type : LinkableRegistry.LinkableType<ConstructLinkable, RenderCentre>(Hexbound.id("construct")) {
        override fun fromNbt(tag: NbtElement, level: ServerWorld): ConstructLinkable? {
            return null
        }

        override fun fromSync(tag: NbtElement, level: World): RenderCentre? {
            return null
        }

        @Suppress("CANNOT_OVERRIDE_INVISIBLE_MEMBER")
        override fun matchSync(centre: ILinkable.IRenderCentre, tag: NbtElement): Boolean {
            return true
        }
    }

    inner class RenderCentre : ILinkable.IRenderCentre {
        override fun colouriser(): FrozenColorizer {
            return FrozenColorizer.DEFAULT.get()
        }

        override fun getLinkableType(): LinkableRegistry.LinkableType<*, *> {
            return Type
        }

        override fun renderCentre(other: ILinkable.IRenderCentre, recursioning: Boolean): Vec3d {
            TODO("Not yet implemented")
        }

        override fun shouldRemove(): Boolean {
            TODO("Not yet implemented")
        }

    }
}

@Suppress("TYPE_PARAMETERS_IN_OBJECT")
object A<T> {

}
