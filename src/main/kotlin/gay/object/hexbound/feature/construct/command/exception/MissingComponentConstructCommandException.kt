package gay.`object`.hexbound.feature.construct.command.exception

import gay.`object`.hexbound.feature.construct.entity.component.ConstructComponentKey
import net.minecraft.text.Text

class MissingComponentConstructCommandException(key: ConstructComponentKey<*>) : ConstructCommandException(
    Text.translatable("hexbound.construct.error.component_missing.${key.key}")
)
