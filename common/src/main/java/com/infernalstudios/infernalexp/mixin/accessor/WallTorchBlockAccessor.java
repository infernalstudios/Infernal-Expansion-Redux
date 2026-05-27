package com.infernalstudios.infernalexp.mixin.accessor;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WallTorchBlock.class)
public interface WallTorchBlockAccessor {

    @Invoker("<init>")
    static WallTorchBlock createWallTorchBlock(SimpleParticleType flameParticle, BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
