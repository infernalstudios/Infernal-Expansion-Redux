package com.infernalstudios.infernalexp.neoforge;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.command.NtpCommand;
import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import com.infernalstudios.infernalexp.config.ClothConfigConstructor;
import com.infernalstudios.infernalexp.neoforge.client.InfernalExpansionNeoForgeClient;
import com.infernalstudios.infernalexp.neoforge.compat.autumnity.AutumnityCompat;
import com.infernalstudios.infernalexp.neoforge.compat.cavernsandchasms.CavernsAndChasmsCompat;
import com.infernalstudios.infernalexp.neoforge.compat.environmental.EnvironmentalCompat;
import com.infernalstudios.infernalexp.platform.Services;
import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(IEConstants.MOD_ID)
public class InfernalExpansionNeoForge {
    public InfernalExpansionNeoForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        IECommon.init();

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(this::onCommandRegister);

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

/*        if (ModList.get().isLoaded("caverns_and_chasms")) {
            CavernsAndChasmsCompat.register(modEventBus);
        }*/

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InfernalExpansionNeoForgeClient::init);
    }

    private void onCommandRegister(RegisterCommandsEvent event) {
        NtpCommand.register(event.getDispatcher());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            if (ModList.get().isLoaded("terrablender")) {
                TerraBlenderCompat.register();
            }

            IECommon.commonSetup();
        });
    }
}