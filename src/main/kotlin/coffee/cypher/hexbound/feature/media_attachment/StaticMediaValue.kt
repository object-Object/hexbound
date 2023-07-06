package coffee.cypher.hexbound.feature.media_attachment

import at.petrak.hexcasting.api.misc.MediaConstants
import kotlinx.serialization.Serializable

@Serializable
data class StaticMediaValue(val priority: Int, val amount: Int, val unit: Unit) {
    enum class Unit(val calculate: (Int) -> Int) {
        ABSOLUTE({ it }),
        DUST({ it * MediaConstants.DUST_UNIT }),
        SHARD({ it * MediaConstants.SHARD_UNIT }),
        CRYSTAL({ it * MediaConstants.CRYSTAL_UNIT })
    }

    val value get() = unit.calculate(amount)
}
