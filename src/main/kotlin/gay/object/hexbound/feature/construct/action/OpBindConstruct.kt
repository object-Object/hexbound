package gay.`object`.hexbound.feature.construct.action

import at.petrak.hexcasting.api.spell.ParticleSpray
import at.petrak.hexcasting.api.spell.RenderedSpell
import at.petrak.hexcasting.api.spell.SpellAction
import at.petrak.hexcasting.api.spell.casting.CastingContext
import at.petrak.hexcasting.api.spell.iota.EntityIota
import at.petrak.hexcasting.api.spell.iota.Iota
import at.petrak.hexcasting.api.spell.iota.NullIota
import at.petrak.hexcasting.api.spell.iota.PatternIota
import at.petrak.hexcasting.api.spell.math.HexPattern
import at.petrak.hexcasting.api.spell.mishaps.MishapInvalidIota
import gay.`object`.hexbound.feature.construct.entity.AbstractConstructEntity
import gay.`object`.hexbound.feature.construct.mishap.MishapConstructForbidden
import gay.`object`.hexbound.util.MemorizedPlayerData
import gay.`object`.hexbound.util.getConstruct
import net.minecraft.entity.player.PlayerEntity

object OpBindConstruct : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, ctx: CastingContext): Triple<RenderedSpell, Int, List<ParticleSpray>> {
        val construct = args.getConstruct(0, argc)
        val req = args[1]

        if (!construct.isPlayerAllowed(ctx.caster)) {
            throw MishapConstructForbidden(construct)
        }

        when (req) {
            is PatternIota -> {
                return Triple(PatternSpell(construct, req.pattern), 0, emptyList())
            }
            is EntityIota -> {
                if (req.entity is PlayerEntity) {
                    return Triple(PlayerSpell(construct, MemorizedPlayerData.forPlayer(req.entity as PlayerEntity)), 0, emptyList())
                }
            }
            is NullIota -> {
                return Triple(UnbindSpell(construct), 0, emptyList())
            }
        }

        throw MishapInvalidIota.of(req, 1, "bindable")
    }

    private class PatternSpell(val construct: AbstractConstructEntity, val pattern: HexPattern) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            construct.boundPattern = pattern
        }
    }

    private class PlayerSpell(val construct: AbstractConstructEntity, val playerData: MemorizedPlayerData) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            construct.boundPlayerData = playerData

        }
    }

    private class UnbindSpell(val construct: AbstractConstructEntity) : RenderedSpell {
        override fun cast(ctx: CastingContext) {
            construct.boundPattern = null
            construct.boundPlayerData = null
        }
    }
}
