package com.infernalstudios.infernalexp.forge;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import com.infernalstudios.infernalexp.forge.client.InfernalExpansionForgeClient;
import com.infernalstudios.infernalexp.platform.Services;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IEConstants.MOD_ID)
public class InfernalExpansion {
    public InfernalExpansion() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        IECommon.init();

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
}