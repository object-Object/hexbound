package coffee.cypher.hexbound.feature.construct.command

import coffee.cypher.hexbound.feature.construct.entity.AbstractConstructEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.util.Identifier

interface ConstructCommand<in T : AbstractConstructEntity> {
    val id: Identifier

    fun serialize(): NbtCompound
}
