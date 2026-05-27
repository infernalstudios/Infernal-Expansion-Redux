package com.infernalstudios.infernalexp.mixin.accessor;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(DoorBlock.class)
public interface DoorBlockAccessor {
    @Invoker("<init>")
    static DoorBlock createDoorBlock(BlockSetType type, BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
