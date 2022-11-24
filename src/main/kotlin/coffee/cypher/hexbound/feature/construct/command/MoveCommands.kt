package coffee.cypher.hexbound.feature.construct.command

import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.init.ConstructCommandTypes
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.Vec3d

@Serializable
class MoveTo(
    @Contextual val targetPos: Vec3d
) : ConstructCommand<AbstractConstructEntity<*>, MoveTo> {
    @Transient
    override val type = ConstructCommandTypes.MOVE_TO

    override fun createGoal(construct: AbstractConstructEntity<*>, serverWorld: ServerWorld): Goal {
        return object : Goal() {
            override fun canStart(): Boolean {
                return construct.pos.distanceTo(targetPos) < 32
            }

            override fun shouldContinue(): Boolean {
                return construct.pos.distanceTo(targetPos) > 1.5
            }

            override fun start() {
                construct.navigation.startMovingTo(targetPos.x, targetPos.y, targetPos.z, 0.25)
            }
        }
    }

}
