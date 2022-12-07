package coffee.cypher.hexbound.feature.construct.broadcasting

import at.petrak.hexcasting.common.lib.HexBlockEntities
import net.minecraft.block.*
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.state.property.Properties
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.math.Vec3d
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.World
import org.quiltmc.qkl.library.blocks.blockSettingsOf
import org.quiltmc.qkl.library.math.plus
import kotlin.jvm.optionals.getOrNull

@Suppress("OVERRIDE_DEPRECATION")
object ConstructBroadcasterBlock : Block(
    blockSettingsOf(
        material = Material.STONE,
        color = MapColor.DEEPSLATE_GRAY,
        soundGroup = BlockSoundGroup.DEEPSLATE_TILES,
        resistance = 4f,
        hardness = 4f
    )
) {
    const val broadcastRadius = 16.0

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(Properties.HORIZONTAL_FACING)
    }

    init {
        defaultState = stateManager.defaultState.with(Properties.HORIZONTAL_FACING, Direction.NORTH)
    }

    override fun hasSidedTransparency(state: BlockState): Boolean {
        return true
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        return defaultState.with(Properties.HORIZONTAL_FACING, ctx.playerFacing.opposite)
    }

    override fun getOutlineShape(
        state: BlockState,
        world: BlockView,
        pos: BlockPos,
        context: ShapeContext
    ): VoxelShape {
        return SHAPE
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun createBroadcastingContext(world: World, blockState: BlockState, pos: BlockPos): BroadcastingContext {
        val slatePos = pos + blockState[Properties.HORIZONTAL_FACING].vector
        val pattern = world.getBlockEntity(slatePos, HexBlockEntities.SLATE_TILE).getOrNull()?.pattern

        return BroadcastingContext(
            Vec3d.ofCenter(pos),
            broadcastRadius,
            pattern,
            Vec3d.ofCenter(pos, 0.8125),
            0.3125
        )
    }

    private val SHAPE = VoxelShapes.union(
        createCuboidShape(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
        createCuboidShape(7.0, 4.0, 7.0, 9.0, 10.0, 9.0),
        createCuboidShape(1.0, 10.0, 1.0, 15.0, 16.0, 15.0)
    )
}
