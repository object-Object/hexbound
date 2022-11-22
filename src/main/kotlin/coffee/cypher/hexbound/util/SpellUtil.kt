package coffee.cypher.hexbound.util

import at.petrak.hexcasting.api.spell.math.HexPattern

val HexPattern.nonBlankSignature: String
    get() = anglesSignature().ifBlank { "empty" }
