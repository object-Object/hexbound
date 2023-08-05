package coffee.cypher.hexbound.util.fakeplayer

import net.minecraft.advancement.Advancement
import net.minecraft.advancement.AdvancementProgress
import net.minecraft.advancement.PlayerAdvancementTracker
import net.minecraft.server.ServerAdvancementLoader
import net.minecraft.server.network.ServerPlayerEntity
import org.quiltmc.loader.api.QuiltLoader

class FakePlayerAdvancementTracker(owner: ServerPlayerEntity?) :
    PlayerAdvancementTracker(null, null, null, QuiltLoader.getConfigDir().toFile().toPath(), owner) {
    override fun clearCriteria() {}

    override fun reload(advancementLoader: ServerAdvancementLoader) {}
    override fun save() {}

    override fun grantCriterion(advancement: Advancement, criterionName: String) = false
    override fun revokeCriterion(advancement: Advancement, criterionName: String) = true

    override fun sendUpdate(player: ServerPlayerEntity) {}
    override fun setDisplayTab(advancement: Advancement?) {}
    override fun getProgress(advancement: Advancement): AdvancementProgress {
        return AdvancementProgress()
    }
}
