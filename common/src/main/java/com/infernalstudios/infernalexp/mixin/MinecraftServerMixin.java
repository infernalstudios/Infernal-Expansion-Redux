package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.mixin.accessor.NoiseGeneratorSettingsAccessor;
import com.infernalstudios.infernalexp.world.surface.ModSurfaceRules;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.storage.WorldData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow public abstract WorldData getWorldData();

    @Inject(method = "createLevels", at = @At("TAIL"))
    private void IE_appendSurfaceRules(ChunkProgressListener holder, CallbackInfo ci) {
        if (this.getWorldData() == null)
            throw new NullPointerException("What! The server's world data is null.");

        MinecraftServer self = (MinecraftServer) (Object) this;

        LevelStem levelStem = self.registries().compositeAccess().registryOrThrow(Registries.LEVEL_STEM).get(LevelStem.NETHER);
        if (levelStem == null)
            throw new NullPointerException(LevelStem.NETHER.location() + " is not a valid level stem key. This is likely the result of a broken level.dat, likely caused by moving this world between MC versions.");

        ChunkGenerator chunkGenerator = levelStem.generator();
        boolean hasNetherBiomes = chunkGenerator.getBiomeSource().possibleBiomes().stream().anyMatch(
                biomeHolder -> biomeHolder.unwrapKey().orElseThrow().location().getNamespace().equals(IEConstants.MOD_ID));
        if (hasNetherBiomes) {
            if (chunkGenerator instanceof NoiseBasedChunkGenerator noiseGenerator) {
                NoiseGeneratorSettings settings = noiseGenerator.generatorSettings().value();

                ((NoiseGeneratorSettingsAccessor) (Object) settings).setSurfaceRule(
                        SurfaceRules.sequence(
                                ModSurfaceRules.addNetherSurfaceRules(), settings.surfaceRule()
                        )
                );

                IEConstants.LOG.info("Successfully added Surface Rules for the Nether");
            }
        }
    }
}