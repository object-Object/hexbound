package coffee.cypher.hexbound.mixinaccessor

import at.petrak.hexcasting.api.spell.casting.CastingContext
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity

interface CastingContextMinionAccessor {
    var `hexbound$construct`: AbstractConstructEntity<*>?
}

@Suppress("CAST_NEVER_SUCCEEDS")
var CastingContext.construct: AbstractConstructEntity<*>?
    get() = (this as CastingContextMinionAccessor).`hexbound$construct`
    set(value) {
        (this as CastingContextMinionAccessor).`hexbound$construct` = value
    }
