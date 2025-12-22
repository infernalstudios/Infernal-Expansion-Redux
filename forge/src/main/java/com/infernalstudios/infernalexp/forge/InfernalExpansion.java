package com.infernalstudios.infernalexp.forge;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import com.infernalstudios.infernalexp.forge.client.InfernalExpansionForgeClient;
import com.infernalstudios.infernalexp.forge.module.*;
import com.infernalstudios.infernalexp.forge.platform.ForgePlatformHelper;
import com.infernalstudios.infernalexp.forge.registration.ForgeRegistrationFactory;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import com.infernalstudios.infernalexp.platform.Services;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IEConstants.MOD_ID)
public class InfernalExpansion {
    public InfernalExpansion() {
        Services.PLATFORM = new ForgePlatformHelper();
        Services.REGISTRATION_FACTORY = new ForgeRegistrationFactory(); // Example

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        IECommon.init();

        modEventBus.register(BlockModuleForge.class);
        modEventBus.register(ItemModuleForge.class);
        modEventBus.register(FeatureModuleForge.class);
        modEventBus.register(CarverModuleForge.class);
        modEventBus.register(EntityTypeModuleForge.class);
        modEventBus.register(EffectModuleForge.class);

        modEventBus.addListener(this::commonSetup);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InfernalExpansionForgeClient::init);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (Services.PLATFORM.isModLoaded("terrablender")) {
                TerraBlenderCompat.register();
            }
        });
    }

    // TODO: refactor into spawn placements module
    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntityTypes.VOLINE.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.REPLACE);
    }
}