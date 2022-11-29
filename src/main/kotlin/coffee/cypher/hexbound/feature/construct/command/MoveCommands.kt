package coffee.cypher.hexbound.feature.construct.command

import coffee.cypher.hexbound.feature.construct.command.execution.ConstructCommandContext
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.feature.construct.command.exception.BadTargetConstructCommandException
import coffee.cypher.hexbound.init.ConstructCommandTypes
import coffee.cypher.kettle.scheduler.TaskContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

@Serializable
class MoveTo(
    @Contextual val targetPos: Vec3d
) : ConstructCommand<MoveTo> {
    override fun getType() = ConstructCommandTypes.MOVE_TO

    override suspend fun TaskContext<out ConstructCommandContext>.execute() {
        withContext {
            maintain {
                if (construct.pos.distanceTo(targetPos) > 32) {
                    throw BadTargetConstructCommandException("pos_too_far", targetPos.x, targetPos.y, targetPos.z)
                }
            }

            construct.navigation.startMovingTo(targetPos.x, targetPos.y, targetPos.z, 1.0)

            waitUntil {
                !construct.isNavigating
            }
        }
    }

    override fun display(world: ServerWorld): Text {
        return Text.translatable("hexbound.construct.command.move_to", targetPos.x, targetPos.y, targetPos.z)
    }
}
