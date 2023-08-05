package coffee.cypher.hexbound.util.fakeplayer

import coffee.cypher.hexbound.mixins.fakeplayer.ClientConnectionAccessor
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.embedded.EmbeddedChannel
import net.minecraft.network.ClientConnection
import net.minecraft.network.NetworkSide
import net.minecraft.network.NetworkState
import net.minecraft.network.PacketSendListener
import net.minecraft.network.listener.PacketListener
import net.minecraft.network.packet.Packet
import net.minecraft.text.Text
import javax.crypto.Cipher

class FakeClientConnection(side: NetworkSide?) : ClientConnection(side) {
    init {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (this as ClientConnectionAccessor).setChannel(EmbeddedChannel())
    }

    override fun channelActive(context: ChannelHandlerContext) {}
    override fun setState(state: NetworkState) {}
    override fun channelInactive(context: ChannelHandlerContext) {}

    @Deprecated("Deprecated in Java")
    override fun exceptionCaught(context: ChannelHandlerContext, ex: Throwable) {}
    override fun channelRead0(channelHandlerContext: ChannelHandlerContext, packet: Packet<*>?) {}
    override fun setPacketListener(listener: PacketListener) {}
    override fun send(packet: Packet<*>?) {}
    override fun send(packet: Packet<*>?, callbacks: PacketSendListener?) {}
    override fun tick() {}
    override fun updateStats() {}
    override fun disconnect(disconnectReason: Text) {}
    override fun setupEncryption(decryptionCipher: Cipher, encryptionCipher: Cipher) {}
    override fun isOpen(): Boolean {
        return false
    }

    override fun hasChannel(): Boolean {
        return true
    }

    override fun getPacketListener(): PacketListener {
        return FakePacketListener.INSTANCE
    }

    override fun disableAutoRead() {}
    override fun setCompressionThreshold(compressionThreshold: Int, rejectsBadPackets: Boolean) {}
    override fun handleDisconnection() {}
    override fun acceptInboundMessage(msg: Any): Boolean {
        return false
    }

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {}
    override fun channelRegistered(ctx: ChannelHandlerContext) {}
    override fun channelUnregistered(ctx: ChannelHandlerContext) {}
    override fun channelReadComplete(ctx: ChannelHandlerContext) {}
    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {}
    override fun channelWritabilityChanged(ctx: ChannelHandlerContext) {}

    companion object {
        val SERVER_FAKE_CONNECTION = FakeClientConnection(NetworkSide.C2S)
    }
}
