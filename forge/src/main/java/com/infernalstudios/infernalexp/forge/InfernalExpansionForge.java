package com.infernalstudios.infernalexp.forge;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.command.NtpCommand;
import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import com.infernalstudios.infernalexp.config.ClothConfigConstructor;
import com.infernalstudios.infernalexp.forge.client.InfernalExpansionForgeClient;
import com.infernalstudios.infernalexp.forge.compat.autumnity.AutumnityCompat;
import com.infernalstudios.infernalexp.forge.compat.cavernsandchasms.CavernsAndChasmsCompat;
import com.infernalstudios.infernalexp.forge.compat.environmental.EnvironmentalCompat;
import com.infernalstudios.infernalexp.platform.Services;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
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
        MinecraftForge.EVENT_BUS.addListener(this::onCommandRegister);

        if (Services.PLATFORM.isModLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(
                    ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) ->
                            AutoConfig.getConfigScreen(ClothConfigConstructor.class, screen).get())
            );
        }

        if (ModList.get().isLoaded("autumnity")) {
            AutumnityCompat.register(modEventBus);
        }

        if (ModList.get().isLoaded("environmental")) {
            EnvironmentalCompat.register();
        }

        if (ModList.get().isLoaded("caverns_and_chasms")) {
            CavernsAndChasmsCompat.register(modEventBus);
        }

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InfernalExpansionForgeClient::init);
    }

    private void onCommandRegister(RegisterCommandsEvent event) {
        NtpCommand.register(event.getDispatcher());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (Services.PLATFORM.isModLoaded("terrablender")) {
                TerraBlenderCompat.register();
            }

            IECommon.commonSetup();
        });
    }
}