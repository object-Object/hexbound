package coffee.cypher.hexbound.feature.construct.command

import coffee.cypher.hexbound.feature.construct.command.exception.BadTargetConstructCommandException
import coffee.cypher.hexbound.feature.construct.command.execution.ConstructCommandContext
import coffee.cypher.hexbound.init.ConstructCommandTypes
import coffee.cypher.kettle.scheduler.TaskContext
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import net.minecraft.server.world.ServerWorld
import net.minecraft.text.Text
import net.minecraft.util.math.Vec3d
import org.quiltmc.qkl.library.text.*
import java.text.DecimalFormat

@Serializable
class MoveTo(
    @Contextual val targetPos: Vec3d
) : ConstructCommand<MoveTo> {
    override fun getType() = ConstructCommandTypes.MOVE_TO

    override suspend fun TaskContext<out ConstructCommandContext>.execute() {
        withContext {
            maintain {
                if (construct.pos.distanceTo(targetPos) > 32) {
                    throw BadTargetConstructCommandException(
                        buildText {
                            color(Color.RED) {
                                //TODO QKLLLLLLL
                                text(
                                    Text.translatable(
                                        "hexbound.vector_format",
                                        DECIMAL_FORMAT.format(targetPos.x),
                                        DECIMAL_FORMAT.format(targetPos.y),
                                        DECIMAL_FORMAT.format(targetPos.z)
                                    )
                                )
                            }

                            translatable("hexbound.construct.exception.bad_target.pos_too_far")
                        }
                    )
                }
            }

            construct.navigation.startMovingTo(targetPos.x, targetPos.y, targetPos.z, 1.0)

            waitUntil {
                !construct.isNavigating
            }
        }
    }

    override fun display(world: ServerWorld): Text {
        return buildText {
            translatable("hexbound.construct.command.move_to")

            color(Color.RED) {
                //TODO QKLLLLLLL
                text(
                    Text.translatable(
                        "hexbound.vector_format",
                        DECIMAL_FORMAT.format(targetPos.x),
                        DECIMAL_FORMAT.format(targetPos.y),
                        DECIMAL_FORMAT.format(targetPos.z)
                    )
                )
            }
        }
    }

    companion object {
        val DECIMAL_FORMAT = DecimalFormat("#0.#")
    }
}
