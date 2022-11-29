package coffee.cypher.hexbound.feature.construct.command.exception

import org.quiltmc.qkl.library.text.buildText
import org.quiltmc.qkl.library.text.translatable

class BadTargetConstructCommandException(stub: String, vararg args: Any) : ConstructCommandException(
    buildText {
        translatable("hexbound.construct.exception.bad_target.$stub", *args)
    }
)
