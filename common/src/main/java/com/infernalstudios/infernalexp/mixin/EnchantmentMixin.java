package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class EnchantmentMixin {

    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void infernalexp$canEnchant(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this == Enchantments.KNOCKBACK && stack.getItem() instanceof BlindsightTongueWhipItem) {
            cir.setReturnValue(true);
        }
    }
}