package com.infernalstudios.infernalexp.block;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.block.entity.VolatileGeyserBlockEntity;
import com.infernalstudios.infernalexp.module.ModBlockEntityTypes;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class VolatileGeyserBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private static final Map<Direction, VoxelShape> SHAPES = new HashMap<>();

    static {
        SHAPES.put(Direction.UP, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 12.0D, 13.0D));
        SHAPES.put(Direction.DOWN, Block.box(3.0D, 4.0D, 3.0D, 13.0D, 16.0D, 13.0D));
        SHAPES.put(Direction.NORTH, Block.box(3.0D, 3.0D, 4.0D, 13.0D, 13.0D, 16.0D));
        SHAPES.put(Direction.SOUTH, Block.box(3.0D, 3.0D, 0.0D, 13.0D, 13.0D, 12.0D));
        SHAPES.put(Direction.WEST, Block.box(4.0D, 3.0D, 3.0D, 16.0D, 13.0D, 13.0D));
        SHAPES.put(Direction.EAST, Block.box(0.0D, 3.0D, 3.0D, 12.0D, 13.0D, 13.0D));
    }

    public VolatileGeyserBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.UP)
                .setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPES.get(Direction.UP));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getClickedFace())
                .setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            boolean isPowered = level.hasNeighborSignal(pos);
            if (isPowered != state.getValue(POWERED)) {
                level.setBlock(pos, state.setValue(POWERED, isPowered), 3);

                if (isPowered && level instanceof ServerLevel serverLevel) {
                    Player nearestPlayer = serverLevel.getNearestPlayer(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 10.0D, false);

                    if (nearestPlayer instanceof ServerPlayer serverPlayer) {
                        ResourceLocation advId = new ResourceLocation(IEConstants.MOD_ID, "nether/pressure_cooker");
                        Advancement advancement = serverLevel.getServer().getAdvancements().getAdvancement(advId);

                        if (advancement != null) {
                            serverPlayer.getAdvancements().award(advancement, "activate_geyser");
                        }
                    }
                }
            }
        }
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (random.nextInt(5) == 0) {
            Direction direction = state.getValue(FACING);

            double x = pos.getX() + 0.5D;
            double y = pos.getY() + 0.5D;
            double z = pos.getZ() + 0.5D;

            double offset = 0.6D;
            x += direction.getStepX() * offset;
            y += direction.getStepY() * offset;
            z += direction.getStepZ() * offset;

            double spread = 0.15D;
            x += (random.nextDouble() - 0.5D) * spread;
            y += (random.nextDouble() - 0.5D) * spread;
            z += (random.nextDouble() - 0.5D) * spread;

            double speed = 0.15D + random.nextDouble() * 0.1D;
            double vx = direction.getStepX() * speed;
            double vy = direction.getStepY() * speed;
            double vz = direction.getStepZ() * speed;

            if (direction.getAxis().isHorizontal()) {
                vy += 0.05D;
            }

            level.addParticle(ParticleTypes.LAVA, x, y, z, vx, vy, vz);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new VolatileGeyserBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntityTypes.VOLATILE_GEYSER.get(), VolatileGeyserBlockEntity::tick);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}