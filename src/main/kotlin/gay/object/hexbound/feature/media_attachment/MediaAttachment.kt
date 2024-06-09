package gay.`object`.hexbound.feature.media_attachment

import at.petrak.hexcasting.api.addldata.ADMediaHolder
import at.petrak.hexcasting.fabric.cc.adimpl.CCMediaHolder
import gay.`object`.hexbound.init.Hexbound
import net.minecraft.item.ItemStack
import net.minecraft.util.registry.Registry
import org.quiltmc.qkl.library.serialization.CodecFactory
import org.quiltmc.qsl.registry.attachment.api.RegistryEntryAttachment

internal val STATIC_MEDIA_ATTACHMENT by lazy {
    RegistryEntryAttachment.builder(
        Registry.ITEM,
        Hexbound.id("media_values"),
        StaticMediaValue::class.java,
        CodecFactory.create<StaticMediaValue>()
    ).build()
}

fun getMediaAttachmentForStack(stack: ItemStack): ADMediaHolder? {
    val attachment = STATIC_MEDIA_ATTACHMENT.getNullable(stack.item) ?: return null

    return CCMediaHolder.Static({ attachment.value }, attachment.priority, stack)
}
