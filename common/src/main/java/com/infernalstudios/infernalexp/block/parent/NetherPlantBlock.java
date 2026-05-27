package com.infernalstudios.infernalexp.block.parent;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class NetherPlantBlock extends BushBlock {
    public NetherPlantBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean mayPlaceOn(BlockState floor, @NotNull BlockGetter world, @NotNull BlockPos pos) {
        return floor.isFaceSturdy(world, pos, Direction.UP);
    }
}
