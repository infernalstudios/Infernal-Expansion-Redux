package com.infernalstudios.infernalexp.mixin.client;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {

    @ModifyConstant(method = "aiStep", constant = @Constant(floatValue = 0.2F))
    private float infernalexp$removeWhipSlowdown(float constant) {
        LocalPlayer player = (LocalPlayer) (Object) this;

        if (player.isUsingItem() && player.getUseItem().getItem() instanceof BlindsightTongueWhipItem) {
            return 0.6F;
        }

        return constant;
    }
}