package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void infernalexp$addWhipEnchantments(int power, ItemStack stack, boolean treasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (stack.getItem() instanceof BlindsightTongueWhipItem) {
            List<EnchantmentInstance> list = cir.getReturnValue();
            Enchantment knockback = Enchantments.KNOCKBACK;

            boolean alreadyHasKnockback = list.stream().anyMatch(e -> e.enchantment == knockback);

            if (!alreadyHasKnockback) {
                for (int level = knockback.getMinLevel(); level <= knockback.getMaxLevel(); level++) {
                    if (power >= knockback.getMinCost(level) && power <= knockback.getMaxCost(level)) {
                        list.add(new EnchantmentInstance(knockback, level));
                    }
                }
            }
        }
    }
}