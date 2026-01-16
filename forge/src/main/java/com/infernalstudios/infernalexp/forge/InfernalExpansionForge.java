package com.infernalstudios.infernalexp.forge;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import com.infernalstudios.infernalexp.config.ClothConfigConstructor;
import com.infernalstudios.infernalexp.forge.client.InfernalExpansionForgeClient;
import com.infernalstudios.infernalexp.platform.Services;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(IEConstants.MOD_ID)
public class InfernalExpansionForge {
    public InfernalExpansionForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        IECommon.init();

        modEventBus.addListener(this::commonSetup);

        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) ->
                            AutoConfig.getConfigScreen(ClothConfigConstructor.class, screen).get())
            );
        }

        if (ModList.get().isLoaded("autumnity")) {
            com.infernalstudios.infernalexp.forge.compat.autumnity.AutumnityCompat.register(modEventBus);
        }

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