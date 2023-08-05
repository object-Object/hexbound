package coffee.cypher.hexbound.util.fakeplayer

import net.minecraft.network.ClientConnection
import net.minecraft.network.PacketSendListener
import net.minecraft.network.message.SignedChatMessage
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.MessageAcknowledgmentC2SPacket
import net.minecraft.network.packet.c2s.play.*
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayNetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

class FakeServerPlayNetworkHandler(
    server: MinecraftServer?,
    connection: ClientConnection?,
    player: ServerPlayerEntity?
) : ServerPlayNetworkHandler(server, connection, player) {
    override fun onPlayerInput(packet: PlayerInputC2SPacket) {}
    override fun onVehicleMove(packet: VehicleMoveC2SPacket) {}
    override fun onTeleportConfirmation(packet: TeleportConfirmationC2SPacket) {}
    override fun onRecipeBookUpdate(packet: RecipeBookUpdateC2SPacket) {}
    override fun onRecipeCategoryOptionUpdate(packet: RecipeCategoryOptionUpdateC2SPacket) {}
    override fun onAdvancementTabOpen(packet: AdvancementTabOpenC2SPacket) {}
    override fun onCommandCompletionRequest(packet: CommandCompletionRequestC2SPacket) {}
    override fun onCommandBlockUpdate(packet: CommandBlockUpdateC2SPacket) {}
    override fun onCommandBlockMinecartUpdate(packet: CommandBlockMinecartUpdateC2SPacket) {}
    override fun onInventoryItemPick(packet: InventoryItemPickC2SPacket) {}
    override fun onItemRename(packet: ItemRenameC2SPacket) {}
    override fun onStructureBlockUpdate(packet: StructureBlockUpdateC2SPacket) {}
    override fun onJigsawUpdate(packet: JigsawUpdateC2SPacket) {}
    override fun onJigsawGeneration(packet: JigsawGenerationC2SPacket) {}
    override fun onMerchantTradeSelection(packet: MerchantTradeSelectionC2SPacket) {}
    override fun onBookUpdate(packet: BookUpdateC2SPacket) {}
    override fun onEntityNbtQuery(packet: EntityNbtQueryC2SPacket) {}
    override fun onBlockNbtQuery(packet: BlockNbtQueryC2SPacket) {}
    override fun onPlayerMove(packet: PlayerMoveC2SPacket) {}
    override fun requestTeleport(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {}
    override fun onPlayerAction(packet: PlayerActionC2SPacket) {}
    override fun onPlayerInteractionWithBlock(packet: PlayerInteractionWithBlockC2SPacket) {}
    override fun onPlayerInteractionWithItem(packet: PlayerInteractionWithItemC2SPacket) {}
    override fun onSpectatorTeleportation(packet: SpectatorTeleportationC2SPacket) {}
    override fun onResourcePackStatusUpdate(packet: ResourcePackStatusUpdateC2SPacket) {}
    override fun onBoatPaddleStateUpdate(packet: BoatPaddleStateUpdateC2SPacket) {}
    override fun onPong(packet: PlayPongC2SPacket) {}
    override fun onDisconnected(reason: Text) {}
    override fun sendPacket(packet: Packet<*>?) {}
    override fun sendPacket(packet: Packet<*>?, callbacks: PacketSendListener?) {}
    override fun onSelectedSlotUpdate(packet: SelectedSlotUpdateC2SPacket) {}
    override fun onChatMessage(packet: ChatMessageC2SPacket) {}
    override fun onHandSwing(packet: HandSwingC2SPacket) {}
    override fun onClientCommand(packet: ClientCommandC2SPacket) {}
    override fun onPlayerInteractionWithEntity(packet: PlayerInteractionWithEntityC2SPacket) {}
    override fun onClientStatusUpdate(packet: ClientStatusUpdateC2SPacket) {}
    override fun onHandledScreenClose(packet: HandledScreenCloseC2SPacket) {}
    override fun onSlotClick(packet: SlotClickC2SPacket) {}
    override fun onCraftRequest(packet: CraftRequestC2SPacket) {}
    override fun onButtonClick(packet: ButtonClickC2SPacket) {}
    override fun onCreativeInventoryAction(packet: CreativeInventoryActionC2SPacket) {}
    override fun onSignUpdate(packet: SignUpdateC2SPacket) {}
    override fun onKeepConnectionAlive(packet: KeepConnectionAliveC2SPacket) {}
    override fun onPlayerAbilityUpdate(packet: PlayerAbilityUpdateC2SPacket) {}
    override fun onClientSettingsUpdate(packet: ClientSettingsUpdateC2SPacket) {}
    override fun onCustomPayload(packet: CustomPayloadC2SPacket) {}
    override fun onDifficultyLockUpdate(packet: DifficultyLockUpdateC2SPacket) {}
    override fun onDifficultyUpdate(packet: DifficultyUpdateC2SPacket) {}
    override fun onBeaconUpdate(packet: BeaconUpdateC2SPacket) {}
    override fun onChatCommand(packet: ChatCommandC2SPacket) {}
    override fun onChatSessionUpdate(packet: ChatSessionUpdateC2SPacket) {}
    override fun onMessageAcknowledgment(packet: MessageAcknowledgmentC2SPacket) {}
    override fun addMessageForValidation(message: SignedChatMessage) {}
    override fun tick() {}
    override fun disconnect(reason: Text) {}
}
