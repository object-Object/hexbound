package coffee.cypher.hexbound.feature.construct.entity

import coffee.cypher.hexbound.util.redirectSpiderLang
import dev.cafeteria.fakeplayerapi.server.FakeServerPlayer
import net.minecraft.entity.EntityType
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.attribute.DefaultAttributeContainer
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import org.quiltmc.qsl.entity.effect.api.StatusEffectRemovalReason
import org.quiltmc.qsl.entity.effect.api.StatusEffectUtils
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
) : AbstractConstructEntity<SpiderConstructEntity>(entityType, world), IAnimatable {
    var heldStack: ItemStack = ItemStack.EMPTY

    private var songSource: BlockPos? = null
    private val animationFactory = GeckoLibUtil.createFactory(this)

    private val animationController = AnimationController(this, "animation_controller", 0f) { event ->
        when {
            event.isMoving -> {
                event.controller.setAnimation(WALK_ANIMATION)

                PlayState.CONTINUE
            }

            event.animatable.canDance() -> {
                event.controller.setAnimation(DANCE_ANIMATION)

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

    //TODO QSL error, remove below 3 methods when patched
    override fun removeStatusEffect(type: StatusEffect, reason: StatusEffectRemovalReason): Boolean {
        return false
    }

    override fun clearStatusEffects(reason: StatusEffectRemovalReason): Int {
        if (world.isClient) {
            return 0
        }

        var removed = 0
        val it = activeStatusEffects.values.iterator()
        while (it.hasNext()) {
            val effect = it.next()
            if (StatusEffectUtils.shouldRemove(this, effect, reason)) {
                it.remove()
                this.onStatusEffectRemoved(effect, reason)
                removed++
            }
        }

        return removed
    }

    override fun onStatusEffectRemoved(effect: StatusEffectInstance, reason: StatusEffectRemovalReason) {
    }

    //up to here

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

    override fun getDefaultName(): Text {
        return redirectSpiderLang(super.getDefaultName())
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
            return createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 1.0)
        }

        private val DANCE_ANIMATION = AnimationBuilder().addAnimation(
            "dance",
            EDefaultLoopTypes.LOOP
        )

        private val WALK_ANIMATION = AnimationBuilder().addAnimation(
            "walk",
            EDefaultLoopTypes.LOOP
        )
    }
}
