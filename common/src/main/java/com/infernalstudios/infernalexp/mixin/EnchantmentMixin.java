package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import com.infernalstudios.infernalexp.module.ModEnchantments;
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
        if (stack.getItem() instanceof BlindsightTongueWhipItem) {
            Enchantment current = (Enchantment) (Object) this;

            if (current == Enchantments.KNOCKBACK || current == Enchantments.FIRE_ASPECT) {
                cir.setReturnValue(true);
            }
            else if (current == ModEnchantments.LASHING.get() ||
                    current == ModEnchantments.DISARMING.get() ||
                    current == ModEnchantments.LEAPING.get() ||
                    current == ModEnchantments.ILLUMINATING.get()) {
                cir.setReturnValue(true);
            }
        }
    }
}