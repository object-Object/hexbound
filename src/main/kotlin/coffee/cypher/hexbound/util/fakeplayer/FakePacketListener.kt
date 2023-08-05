package coffee.cypher.hexbound.util.fakeplayer

import net.minecraft.network.ClientConnection
import net.minecraft.network.listener.PacketListener
import net.minecraft.text.Text

@JvmRecord
data class FakePacketListener(val connection: ClientConnection) : PacketListener {
    override fun onDisconnected(reason: Text) {}
    override fun isConnected(): Boolean {
        return true
    }

    companion object {
        val INSTANCE = FakePacketListener(FakeClientConnection.SERVER_FAKE_CONNECTION)
    }
}
