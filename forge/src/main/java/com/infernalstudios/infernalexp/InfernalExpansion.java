package com.infernalstudios.infernalexp;

import com.infernalstudios.infernalexp.client.InfernalExpansionForgeClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

@Mod(IEConstants.MOD_ID)
public class InfernalExpansion {
    public InfernalExpansion() {
        IECommon.init();
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> InfernalExpansionForgeClient::init);


    }
}