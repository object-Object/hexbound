package gay.`object`.hexbound.util.mixinaccessor

import at.petrak.hexcasting.api.spell.casting.CastingContext
import gay.`object`.hexbound.feature.construct.entity.AbstractConstructEntity

interface CastingContextConstructAccessor {
    var `hexbound$construct`: AbstractConstructEntity?
}

@Suppress("CAST_NEVER_SUCCEEDS")
var CastingContext.construct: AbstractConstructEntity?
    get() = (this as CastingContextConstructAccessor).`hexbound$construct`
    set(value) {
        (this as CastingContextConstructAccessor).`hexbound$construct` = value
    }
