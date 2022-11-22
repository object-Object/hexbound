package coffee.cypher.hexbound.mixinaccessor

import at.petrak.hexcasting.common.blocks.entity.BlockEntityStoredPlayerImpetus

interface ImpetusFakePlayerAccessor {
    var `hexbound$useFakeFallback`: Boolean
}

var BlockEntityStoredPlayerImpetus.useFakeFallback: Boolean
    get() = (this as ImpetusFakePlayerAccessor).`hexbound$useFakeFallback`
    set(value) {
        (this as ImpetusFakePlayerAccessor).`hexbound$useFakeFallback` = value
    }
