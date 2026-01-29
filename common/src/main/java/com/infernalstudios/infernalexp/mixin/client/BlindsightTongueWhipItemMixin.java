package com.infernalstudios.infernalexp.mixin.client;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlindsightTongueWhipItem.class)
public class BlindsightTongueWhipItemMixin {

    @Inject(method = "getUseAnimation", at = @At("HEAD"), cancellable = true)
    private void infernalexp$switchAnimFirstPerson(ItemStack stack, CallbackInfoReturnable<UseAnim> cir) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player != null && mc.player.isUsingItem() && mc.player.getUseItem() == stack) {
            if (mc.options.getCameraType().isFirstPerson()) {
                cir.setReturnValue(UseAnim.BOW);
            }
        }
    }
}