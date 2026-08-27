package com.infernalstudios.infernalexp.neoforge.module;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.entities.*;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;

@EventBusSubscriber(modid = IEConstants.MOD_ID)
public class SpawnPlacementModuleNeoForge {

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntityTypes.VOLINE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VolineEntity::checkVolineSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.GLOWSQUITO.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsquitoEntity::checkGlowsquitoSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.BLINDSIGHT.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlindsightEntity::checkBlindsightSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.GLOWSILK_MOTH.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsilkMothEntity::checkGlowsilkMothSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.WARPBEETLE.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WarpbeetleEntity::checkWarpbeetleSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }
}