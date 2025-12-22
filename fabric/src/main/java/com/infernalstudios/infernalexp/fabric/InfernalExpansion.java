package com.infernalstudios.infernalexp.fabric;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import com.infernalstudios.infernalexp.fabric.module.*;
import com.infernalstudios.infernalexp.fabric.platform.FabricPlatformHelper;
import com.infernalstudios.infernalexp.fabric.registration.FabricRegistrationFactory;
import com.infernalstudios.infernalexp.module.*;
import com.infernalstudios.infernalexp.platform.Services;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;

public class InfernalExpansion implements ModInitializer {

    @Override
    public void onInitialize() {
        Services.PLATFORM = new FabricPlatformHelper();
        Services.REGISTRATION_FACTORY = new FabricRegistrationFactory();

        IECommon.init();

        BlockModuleFabric.registerBlocks();
        ItemModuleFabric.registerItems();
        EntityTypeModuleFabric.registerEntities();
        EffectModuleFabric.registerEffects();
        FeatureModuleFabric.registerFeatures();
        CarverModuleFabric.registerCarvers();

        // TODO: refactor into spawn placements module and call SpawnPlacementsModuleFabric.registerSpawns();
        SpawnPlacements.register(ModEntityTypes.VOLINE.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
    }
}