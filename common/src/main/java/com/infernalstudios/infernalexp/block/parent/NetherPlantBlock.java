package com.infernalstudios.infernalexp.block.parent;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NetherPlantBlock extends BushBlock {
    public static final MapCodec<NetherPlantBlock> CODEC = simpleCodec(NetherPlantBlock::new);

    public NetherPlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean mayPlaceOn(BlockState floor, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return floor.isFaceSturdy(world, pos, Direction.UP);
    }
}
