package com.infernalstudios.infernalexp.fabric.module;

import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;

public class SpawnPlacementModuleFabric {
    public static void registerSpawnPlacements() {
        SpawnPlacements.register(ModEntityTypes.VOLINE.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VolineEntity::checkVolineSpawnRules);

        SpawnPlacements.register(ModEntityTypes.GLOWSQUITO.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsquitoEntity::checkGlowsquitoSpawnRules);

        SpawnPlacements.register(ModEntityTypes.GLOWSILK_MOTH.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsilkMothEntity::checkGlowsilkMothSpawnRules);
    }
}