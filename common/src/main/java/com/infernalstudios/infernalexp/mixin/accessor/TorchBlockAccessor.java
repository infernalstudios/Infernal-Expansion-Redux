package com.infernalstudios.infernalexp.mixin.accessor;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TorchBlock.class)
public interface TorchBlockAccessor {

    @Invoker("<init>")
    static TorchBlock createTorchBlock(SimpleParticleType flameParticle, BlockBehaviour.Properties properties) {
        throw new UnsupportedOperationException();
    }
}
