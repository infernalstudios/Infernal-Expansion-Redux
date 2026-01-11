package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModBiomes;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.structures.RuinedPortalStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(RuinedPortalStructure.class)
public class RuinedPortalStructureMixin {

    @Inject(method = "findGenerationPoint", at = @At("HEAD"), cancellable = true)
    private void infernalexp$preventGlowstoneCanyonPortals(
            Structure.GenerationContext context,
            CallbackInfoReturnable<Optional<Structure.GenerationStub>> cir) {

        if (context.chunkGenerator().getBiomeSource()
                .getNoiseBiome(
                        context.chunkPos().getMinBlockX() >> 2,
                        0,
                        context.chunkPos().getMinBlockZ() >> 2,
                        context.randomState().sampler()
                ).is(ModBiomes.GLOWSTONE_CANYON)) {
            cir.setReturnValue(Optional.empty());
        }
    }
}