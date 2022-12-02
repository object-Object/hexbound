package coffee.cypher.hexbound.feature.construct.command.exception

import net.minecraft.text.Text
import org.quiltmc.qkl.library.text.buildText
import org.quiltmc.qkl.library.text.translatable

class BadTargetConstructCommandException(text: Text) : ConstructCommandException(text) {
    constructor(stub: String, vararg args: Any) : this(
        buildText {
            translatable("hexbound.construct.exception.bad_target.$stub", *args)
        }
    )
}
