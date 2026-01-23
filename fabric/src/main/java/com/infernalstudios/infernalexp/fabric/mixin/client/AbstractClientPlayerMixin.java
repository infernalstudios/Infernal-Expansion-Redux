package com.infernalstudios.infernalexp.fabric.mixin.client;

import com.infernalstudios.infernalexp.module.ModItems;
import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class AbstractClientPlayerMixin {

    @Inject(method = "getFieldOfViewModifier", at = @At("TAIL"), cancellable = true)
    private void infernalExp$glowsilkBowFOVModifier(CallbackInfoReturnable<Float> cir) {
        AbstractClientPlayer player = (AbstractClientPlayer) (Object) this;
        if (player.isUsingItem() && player.getUseItem().is(ModItems.GLOWSILK_BOW.get())) {
            float fovModifier = player.getTicksUsingItem() / 20.0F;

            if (fovModifier > 1.0F) {
                fovModifier = 1.0F;
            } else {
                fovModifier *= fovModifier;
            }

            cir.setReturnValue(cir.getReturnValue() * (1.0F - (fovModifier * 0.15F)));
        }
    }
}