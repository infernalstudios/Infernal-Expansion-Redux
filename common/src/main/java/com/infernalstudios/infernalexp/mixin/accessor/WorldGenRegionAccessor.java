package com.infernalstudios.infernalexp.mixin.accessor;

import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WorldGenRegion.class)
public interface WorldGenRegionAccessor {
    @Accessor
    int getWriteRadiusCutoff();

    @Accessor
    ChunkAccess getCenter();
}
