package com.infernalstudios.infernalexp.forge.events;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.sound.GlowsquitoFlightSound;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IEConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity() instanceof GlowsquitoEntity glowsquito) {
            Minecraft.getInstance().getSoundManager().play(new GlowsquitoFlightSound(glowsquito));
        }
    }
}