package coffee.cypher.hexbound.feature.construct.command

import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import coffee.cypher.hexbound.init.ConstructCommandTypes
import com.mojang.serialization.Codec
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.entity.ai.goal.Goal
import net.minecraft.server.world.ServerWorld

interface ConstructCommand<in T : AbstractConstructEntity<*>, C : ConstructCommand<T, C>> {
    val type: Type<C>

    fun createGoal(construct: T, serverWorld: ServerWorld): Goal?

    data class Type<C : ConstructCommand<*, C>>(
        val codec: Codec<C>
    )
}

@Serializable
class NoOpCommand : ConstructCommand<AbstractConstructEntity<*>, NoOpCommand> {
    @Transient
    override val type = ConstructCommandTypes.NO_OP

    override fun createGoal(construct: AbstractConstructEntity<*>, serverWorld: ServerWorld): Goal? {
        return null
    }
}
