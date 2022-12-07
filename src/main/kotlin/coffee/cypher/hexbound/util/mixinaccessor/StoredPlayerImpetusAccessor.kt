package coffee.cypher.hexbound.util.mixinaccessor

import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus
import coffee.cypher.hexbound.util.MemorizedPlayerData
import java.util.UUID

interface StoredPlayerImpetusAccessor {
    var `hexbound$useFakeFallback`: Boolean

    val `hexbound$storedPlayerUuid`: UUID

    val `hexbound$memorizedPlayer`: MemorizedPlayerData?
}

var BlockEntityStoredPlayerImpetus.useFakeFallback: Boolean
    get() = (this as StoredPlayerImpetusAccessor).`hexbound$useFakeFallback`
    set(value) {
        (this as StoredPlayerImpetusAccessor).`hexbound$useFakeFallback` = value
    }

val BlockEntityStoredPlayerImpetus.storedPlayerUuid: UUID
    get() = (this as StoredPlayerImpetusAccessor).`hexbound$storedPlayerUuid`

val BlockEntityStoredPlayerImpetus.memorizedPlayer: MemorizedPlayerData?
    get() = (this as StoredPlayerImpetusAccessor).`hexbound$memorizedPlayer`
