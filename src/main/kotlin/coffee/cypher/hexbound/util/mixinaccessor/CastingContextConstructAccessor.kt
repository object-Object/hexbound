package coffee.cypher.hexbound.util.mixinaccessor

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity

interface CastingEnvironmentConstructAccessor {
    var `hexbound$construct`: AbstractConstructEntity?
}

@Suppress("CAST_NEVER_SUCCEEDS")
var CastingEnvironment.construct: AbstractConstructEntity?
    get() = (this as CastingEnvironmentConstructAccessor).`hexbound$construct`
    set(value) {
        (this as CastingEnvironmentConstructAccessor).`hexbound$construct` = value
    }
