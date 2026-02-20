package com.infernalstudios.infernalexp.forge.module;

import com.infernalstudios.infernalexp.entities.*;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "infernalexp", bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpawnPlacementModuleForge {

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntityTypes.VOLINE.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VolineEntity::checkVolineSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.GLOWSQUITO.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsquitoEntity::checkGlowsquitoSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.BLINDSIGHT.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                BlindsightEntity::checkBlindsightSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.GLOWSILK_MOTH.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                GlowsilkMothEntity::checkGlowsilkMothSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );

        event.register(ModEntityTypes.WARPBEETLE.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WarpbeetleEntity::checkWarpbeetleSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }
}