package com.infernalstudios.infernalexp.neoforge;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.command.NtpCommand;
import com.infernalstudios.infernalexp.compat.TerraBlenderCompat;
import com.infernalstudios.infernalexp.neoforge.client.InfernalExpansionNeoForgeClient;
import com.infernalstudios.infernalexp.neoforge.compat.autumnity.AutumnityCompat;
import com.infernalstudios.infernalexp.neoforge.compat.environmental.EnvironmentalCompat;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(IEConstants.MOD_ID)
public class InfernalExpansionNeoForge {
    public InfernalExpansionNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        IECommon.init();

        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(this::onCommandRegister);

        if (ModList.get().isLoaded("autumnity")) {
            AutumnityCompat.register(modEventBus);
        }

        if (ModList.get().isLoaded("environmental")) {
            EnvironmentalCompat.register();
        }

/*        if (ModList.get().isLoaded("caverns_and_chasms")) {
            CavernsAndChasmsCompat.register(modEventBus);
        }*/

        if (FMLEnvironment.dist.isClient()) {
            InfernalExpansionNeoForgeClient.init(modEventBus, modContainer);
        }
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