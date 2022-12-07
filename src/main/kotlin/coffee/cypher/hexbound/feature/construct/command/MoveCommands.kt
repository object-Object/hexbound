package coffee.cypher.hexbound.feature.construct.command

import coffee.cypher.hexbound.feature.construct.command.exception.BadTargetConstructCommandException
import coffee.cypher.hexbound.feature.construct.command.execution.ConstructCommandContext
import coffee.cypher.hexbound.init.HexboundData
import coffee.cypher.hexbound.util.formatVector
import coffee.cypher.kettle.scheduler.TaskContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d

@Serializable
class MoveTo(
    @Contextual val targetPos: Vec3d
) : ConstructCommand<MoveTo> {
    override fun getType() = HexboundData.ConstructCommandTypes.MOVE_TO

    override suspend fun TaskContext<out ConstructCommandContext>.execute() {
        withContext {
            maintain {
                if (construct.pos.distanceTo(targetPos) > 32) {
                    throw BadTargetConstructCommandException(targetPos, "pos_too_far")
                }
            }

            val path = construct.navigation.findPathTo(targetPos.x, targetPos.y, targetPos.z, 1)
                       ?: throw BadTargetConstructCommandException(targetPos, "no_path_found")

            construct.navigation.startMovingAlong(path, 1.0)

            waitUntil {
                !construct.isNavigating
            }
        }
    }

    override fun display(world: ServerWorld): Text {
        return Text.translatable("hexbound.construct.command.move_to", formatVector(targetPos))
    }
}
