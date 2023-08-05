package coffee.cypher.hexbound.feature.pigment_storage.action

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.math.HexPattern
import coffee.cypher.hexbound.feature.pigment_storage.mishap.MishapMissingPigmentKey
import coffee.cypher.hexbound.init.memorizedPigments
import coffee.cypher.hexbound.util.nonBlankSignature
import coffee.cypher.hexbound.util.requireCaster
import net.minecraft.server.network.ServerPlayerEntity

object OpPigmentDelete : SpellAction {
    override val argc = 1

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val pattern = args.getPattern(0, 1)
        val caster = env.requireCaster()

        if (pattern.nonBlankSignature !in caster.memorizedPigments) {
            throw MishapMissingPigmentKey(pattern)
        }

        return SpellAction.Result(
            Spell(pattern, caster),
            0,
            emptyList()
        )
    }

    private data class Spell(val key: HexPattern, val caster: ServerPlayerEntity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            caster.memorizedPigments.remove(key.nonBlankSignature)
        }
    }
}
