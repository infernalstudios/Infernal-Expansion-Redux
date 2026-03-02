package com.infernalstudios.infernalexp.mixin.client;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @ModifyExpressionValue(method = "aiStep", at = @At(value = "CONSTANT", args = "floatValue=0.2"))
    private float infernalexp$removeWhipSlowdown(float original) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        if (player.isUsingItem() && player.getUseItem().getItem() instanceof BlindsightTongueWhipItem) {
            return 0.6F;
        }

        return original;
    }
}