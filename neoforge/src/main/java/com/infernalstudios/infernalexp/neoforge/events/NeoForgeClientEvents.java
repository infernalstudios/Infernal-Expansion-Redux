package com.infernalstudios.infernalexp.neoforge.events;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.sound.GlowsquitoFlightSound;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import com.infernalstudios.infernalexp.module.ModItems;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;

@EventBusSubscriber(modid = IEConstants.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class NeoForgeClientEvents {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity() instanceof GlowsquitoEntity glowsquito) {
            Minecraft.getInstance().getSoundManager().play(new GlowsquitoFlightSound(glowsquito));
        }
    }

    @SubscribeEvent
    public static void glowsilkBowFOVModifier(ComputeFovModifierEvent event) {
        if (event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().is(ModItems.GLOWSILK_BOW.get())) {
            float fovModifier = event.getPlayer().getTicksUsingItem() / 20.0F;

            if (fovModifier > 1.0F) {
                fovModifier = 1.0F;
            } else {
                fovModifier *= fovModifier;
            }

            event.setNewFovModifier(event.getFovModifier() * (1.0F - (fovModifier * 0.15F)));
        }
    }
}