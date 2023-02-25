package coffee.cypher.hexbound.feature.combat.shield

import at.petrak.hexcasting.api.misc.FrozenColorizer
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import coffee.cypher.hexbound.util.provideDelegate
import net.minecraft.entity.*
import net.minecraft.entity.data.DataTracker
import net.minecraft.entity.data.TrackedData
import net.minecraft.entity.data.TrackedDataHandlerRegistry
import net.minecraft.nbt.NbtCompound
import net.minecraft.network.Packet
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.util.Util
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.quiltmc.qkl.library.math.minus
import org.quiltmc.qkl.library.math.plus
import org.quiltmc.qkl.library.math.times
import org.quiltmc.qkl.library.math.unaryMinus
import org.quiltmc.qsl.entity.api.QuiltEntityTypeBuilder
import java.util.function.BiFunction
import kotlin.math.abs

class ShieldEntity(type: EntityType<ShieldEntity>, world: World) : Entity(type, world) {
    var colorizerTag by COLORIZER

    private val colorizerMemo = Util.memoize(FrozenColorizer::fromNBT)
    private val basisMemo = Util.memoize(BiFunction(ShieldEntity::calculateBasis))

    private var lockedPosition: Triple<Vec3d, Float, Float>? = null

    fun lockPosition() {
        lockedPosition = Triple(pos, pitch, yaw)
    }

    var colorizer: FrozenColorizer
        get() = colorizerMemo.apply(colorizerTag)
        set(value) {
            colorizerTag = value.serializeToNBT()
        }

    override fun getHeadYaw(): Float {
        return yaw
    }

    override fun shouldSave(): Boolean {
        return false
    }

    override fun collides(): Boolean {
        return true
    }

    override fun initDataTracker() {
        dataTracker.startTracking(COLORIZER, FrozenColorizer.DEFAULT.get().serializeToNBT())
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
    }

    override fun createSpawnPacket(): Packet<*> {
        return EntitySpawnS2CPacket(this)
    }

    override fun getEyeHeight(pose: EntityPose, dimensions: EntityDimensions): Float {
        return dimensions.height / 2
    }

    override fun calculateBoundingBox(): Box {
        val (forward, up, right) = getBasis()
        val center = pos + Vec3d(0.0, 1.3125, 0.0)
        val halfDiagonal = right * 1.5 + up * 1.3125 + forward * 0.05
        return Box(center - halfDiagonal, center + halfDiagonal)
    }

    fun getBasis(): Triple<Vec3d, Vec3d, Vec3d> {
        if (basisMemo == null) {
            return calculateBasis(pitch, yaw)
        }
        return basisMemo.apply(pitch, yaw)
    }

    override fun tick() {
        super.tick()
        if (age == DEPLOY_TIME && world.isClient) {
            val (_, up, right) = getBasis()
            val colorizer = colorizer

            listOf(
                -right * 1.5 + up * 1.625,
                -right * 1.5 + up,
                right * 1.5 + up * 1.625,
                right * 1.5 + up
            ).forEach {
                world.addParticle(
                    ConjureParticleOptions(colorizer.getColor(world.time.toFloat(), it), true),
                    x + it.x, y + it.y, z + it.z,
                    0.0, 0.0, 0.0
                )
            }
        }

        lockedPosition?.let { (lockedPos, lockedPitch, lockedYaw) ->
            if (lockedPos.distanceTo(pos) > 0.1 || abs(pitch - lockedPitch) % 360 > 2 || abs(yaw - lockedYaw) % 360 > 2) {
                discard()
            }
        }
    }

    companion object {
        const val DEPLOY_TIME = 3

        val COLORIZER: TrackedData<NbtCompound> =
            DataTracker.registerData(ShieldEntity::class.java, TrackedDataHandlerRegistry.TAG_COMPOUND)

        fun createType(): EntityType<ShieldEntity> {
            return QuiltEntityTypeBuilder
                .create<ShieldEntity>()
                .spawnGroup(SpawnGroup.MISC)
                .entityFactory(::ShieldEntity)
                .makeFireImmune()
                .disableSaving()
                .disableSummon()
                .setDimensions(EntityDimensions.fixed(3f, 2.625f))
                .build()
        }

        @JvmStatic
        fun canBypassShieldForDirection(direction: Vec3d, shield: Entity): Boolean {
            if (shield !is ShieldEntity) {
                return false
            }

            return direction.dotProduct(Vec3d.fromPolar(shield.pitch, shield.yaw)) >= 0
        }

        private fun calculateBasis(pitch: Float, yaw: Float): Triple<Vec3d, Vec3d, Vec3d> {
            val forward = Vec3d.fromPolar(pitch, yaw)

            val up = Vec3d(0.0, 1.0, 0.0)
                .rotateX(pitch * Math.PI.toFloat() / 180f)
                .rotateY(yaw * Math.PI.toFloat() / 180f)

            val right = forward.crossProduct(up)

            return Triple(forward, up, right)
        }
    }
}
