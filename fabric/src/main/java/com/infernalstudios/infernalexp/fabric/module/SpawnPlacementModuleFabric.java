package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.entities.*;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public class SpawnPlacementModuleFabric {
    public static void registerSpawnPlacements() {
        SpawnPlacements.register(ModEntityTypes.VOLINE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VolineEntity::checkVolineSpawnRules);

        SpawnPlacements.register(ModEntityTypes.GLOWSQUITO.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsquitoEntity::checkGlowsquitoSpawnRules);

        SpawnPlacements.register(ModEntityTypes.GLOWSILK_MOTH.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsilkMothEntity::checkGlowsilkMothSpawnRules);

        SpawnPlacements.register(ModEntityTypes.BLINDSIGHT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlindsightEntity::checkBlindsightSpawnRules);

        SpawnPlacements.register(ModEntityTypes.WARPBEETLE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WarpbeetleEntity::checkWarpbeetleSpawnRules);
    }
}