package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.block.ShroomlightTearBlock;
import com.infernalstudios.infernalexp.compat.NetherExpCompat;
import com.infernalstudios.infernalexp.compat.ShroomlightCompatRegistry;
import com.infernalstudios.infernalexp.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Feature.class)
public abstract class FeatureMixin {
    @Shadow
    protected abstract void setBlock(LevelWriter $$0, BlockPos $$1, BlockState $$2);

    @Inject(method = "setBlock", at = @At("HEAD"))
    public void addShroomlightTears(LevelWriter level, BlockPos pos, BlockState state, CallbackInfo ci) {
        if ((Object) this instanceof HugeFungusFeature) {
            infernalexp$tryPlaceTears(level, pos, state);
            return;
        }

        if (Services.PLATFORM.isModLoaded("netherexp") && NetherExpCompat.isNetherExpFungus(this)) {
            infernalexp$tryPlaceTears(level, pos, state);
        }
    }

    @Unique
    private void infernalexp$tryPlaceTears(LevelWriter level, BlockPos pos, BlockState state) {
        if (level instanceof LevelReader world) {
            Block tearBlock = ShroomlightCompatRegistry.getTearVariant(state.getBlock());

            if (tearBlock != null) {
                boolean reversed = world.getBiome(pos).is(Biomes.WARPED_FOREST);
                BlockPos target = reversed ? pos.above() : pos.below();

                if (world.isEmptyBlock(target) && Math.random() < 0.5) {
                    this.setBlock(level, target, tearBlock.defaultBlockState()
                            .setValue(ShroomlightTearBlock.UP, reversed));
                }
            }
        }
    }
}