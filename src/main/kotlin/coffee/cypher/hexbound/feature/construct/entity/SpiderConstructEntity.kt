package coffee.cypher.hexbound.feature.construct.entity

import coffee.cypher.hexbound.init.Hexbound
import dev.cafeteria.fakeplayerapi.server.FakeServerPlayer
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Hand
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.PlayState
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes
import software.bernie.geckolib3.core.controller.AnimationController
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.util.GeckoLibUtil

class SpiderConstructEntity(
    entityType: EntityType<SpiderConstructEntity>,
    world: World
) : AbstractConstructEntity(entityType, world), IAnimatable {
    var heldStack: ItemStack = ItemStack.EMPTY

    private var songSource: BlockPos? = null
    private val animationFactory = GeckoLibUtil.createFactory(this)

    private val animationController = AnimationController(this, "animation_controller", 0f) { event ->
        when {
            event.isMoving -> {
                event.controller.setAnimation(
                    AnimationBuilder().addAnimation(
                        "animation.construct.walk",
                        EDefaultLoopTypes.LOOP
                    )
                )

                PlayState.CONTINUE
            }

            event.animatable.canDance() -> {
                event.controller.setAnimation(
                    AnimationBuilder().addAnimation(
                        "animation.construct.dance",
                        EDefaultLoopTypes.LOOP
                    )
                )

                PlayState.CONTINUE
            }

            else -> PlayState.STOP
        }
    }

    override fun prepareFakePlayer(world: ServerWorld): FakeServerPlayer {
        return super.prepareFakePlayer(world).also {
            it.setStackInHand(Hand.MAIN_HAND, heldStack)
        }
    }

    override fun equipStack(slot: EquipmentSlot, stack: ItemStack) {
        if (slot == EquipmentSlot.MAINHAND) {
            heldStack = stack
        }
    }

    override fun getEquippedStack(slot: EquipmentSlot): ItemStack {
        return when (slot) {
            EquipmentSlot.MAINHAND -> heldStack
            else -> ItemStack.EMPTY
        }
    }

    override fun setNearbySongPlaying(songPosition: BlockPos?, playing: Boolean) {
        songSource = if (playing)
            songPosition
        else
            null
    }

    private fun canDance(): Boolean {
        val dist = songSource?.getSquaredDistanceToCenter(x, y, z)

        return dist != null && dist <= 64 && command == null
    }

    override fun registerControllers(data: AnimationData) {
        data.addAnimationController(animationController)
    }

    override fun getFactory(): AnimationFactory {
        return animationFactory
    }

    override fun writeCustomDataToNbt(nbt: NbtCompound) {
        super.writeCustomDataToNbt(nbt)

        nbt.put("held_stack", heldStack.writeNbt(NbtCompound()))
    }

    override fun readCustomDataFromNbt(nbt: NbtCompound) {
        super.readCustomDataFromNbt(nbt)

        heldStack = ItemStack.fromNbt(nbt.getCompound("held_stack"))
    }

    companion object {
        fun createAttributes(): DefaultAttributeContainer.Builder {
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0)
        }
    }

    //TODO clean this the hell up
    abstract class Command {
        abstract val goal: Goal
        abstract val id: Identifier
        abstract fun serialize(): NbtCompound

        companion object {
            val COMMAND_READERS = mutableMapOf(
                Hexbound.id("pick_up") to { constructEntity: SpiderConstructEntity, nbt: NbtCompound, world: ServerWorld ->
                    if (nbt.containsUuid("target")) {
                        val target = world.getEntity(nbt.getUuid("target")) as? ItemEntity

                        target?.let { PickUp(target, constructEntity) }
                    } else {
                        null
                    }
                },
                Hexbound.id("drop_off") to { constructEntity, _, _ ->
                    DropOff(constructEntity)
                },
                Hexbound.id("move_to") to { constructEntity, nbt, _ ->
                    if (nbt.contains("x") && nbt.contains("y") && nbt.contains("z")) {
                        MoveTo(Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z")), constructEntity)
                    } else {
                        null
                    }
                }
            )
        }

        class PickUp(val target: ItemEntity, constructEntity: SpiderConstructEntity) : Command() {
            override val goal = object : Goal() {
                override fun canStart(): Boolean {
                    return !target.cannotPickup() &&
                           target.isAlive &&
                           constructEntity.pos.distanceTo(target.pos) < 2 &&
                           constructEntity.heldStack.isEmpty
                }

                override fun tick() {
                    constructEntity.heldStack = target.stack
                    target.discard()
                }
            }

            override val id = Hexbound.id("pick_up")

            override fun serialize(): NbtCompound {
                return NbtCompound().apply {
                    putUuid("target", target.uuid)
                }
            }
        }

        class DropOff(constructEntity: SpiderConstructEntity) : Command() {
            override val goal = object : Goal() {
                override fun canStart(): Boolean {
                    return !constructEntity.heldStack.isEmpty
                }

                override fun tick() {
                    constructEntity.world.spawnEntity(
                        ItemEntity(
                            constructEntity.world,
                            constructEntity.x,
                            constructEntity.y,
                            constructEntity.z,
                            constructEntity.heldStack,
                            0.0,
                            0.0,
                            0.0
                        )
                    )
                    constructEntity.heldStack = ItemStack.EMPTY
                }
            }

            override val id = Hexbound.id("drop_off")

            override fun serialize(): NbtCompound {
                return NbtCompound()
            }
        }

        class MoveTo(val pos: Vec3d, constructEntity: SpiderConstructEntity) : Command() {
            override val goal = object : Goal() {
                override fun canStart(): Boolean {
                    return constructEntity.pos.distanceTo(pos) < 32
                }

                override fun shouldContinue(): Boolean {
                    return constructEntity.pos.distanceTo(pos) > 1.5
                }

                override fun start() {
                    constructEntity.navigation.startMovingTo(pos.x, pos.y, pos.z, 0.25)
                }
            }

            override val id = Hexbound.id("move_to")

            override fun serialize(): NbtCompound {
                return NbtCompound().apply {
                    putDouble("x", pos.x)
                    putDouble("y", pos.y)
                    putDouble("z", pos.z)
                }
            }
        }
    }
}
