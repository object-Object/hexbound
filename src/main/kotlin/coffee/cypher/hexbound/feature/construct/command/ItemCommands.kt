package coffee.cypher.hexbound.feature.construct.command

import coffee.cypher.hexbound.feature.construct.entity.SpiderConstructEntity
import coffee.cypher.hexbound.init.ConstructCommandTypes
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.ItemEntity
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import java.util.*

@Serializable
class PickUp(
    @Contextual val targetUuid: UUID
) : ConstructCommand<SpiderConstructEntity, PickUp> {
    @Transient
    override val type = ConstructCommandTypes.PICK_UP

    override fun createGoal(construct: SpiderConstructEntity, serverWorld: ServerWorld): Goal? {
        val target = serverWorld.getEntity(targetUuid) as? ItemEntity ?: return null


        return object : Goal() {

            override fun canStart(): Boolean {
                return !target.cannotPickup() &&
                       target.isAlive &&
                       construct.pos.distanceTo(target.pos) < 2 &&
                       construct.heldStack.isEmpty
            }

            override fun tick() {
                construct.heldStack = target.stack
                target.discard()
            }
        }
    }
}

@Serializable
class DropOff : ConstructCommand<SpiderConstructEntity, DropOff> {
    @Transient
    override val type = ConstructCommandTypes.DROP_OFF

    override fun createGoal(construct: SpiderConstructEntity, serverWorld: ServerWorld): Goal? {
        if (construct.heldStack.isEmpty) {
            return null
        }

        return object : Goal() {
            override fun canStart(): Boolean {
                return !construct.heldStack.isEmpty
            }

            override fun tick() {
                construct.world.spawnEntity(
                    ItemEntity(
                        serverWorld,
                        construct.x,
                        construct.y,
                        construct.z,
                        construct.heldStack,
                        0.0,
                        0.0,
                        0.0
                    )
                )
                construct.heldStack = ItemStack.EMPTY
            }
        }
    }

}
