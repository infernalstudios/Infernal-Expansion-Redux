package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import com.infernalstudios.infernalexp.module.ModEnchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getAvailableEnchantmentResults", at = @At("RETURN"))
    private static void infernalexp$addWhipEnchantments(int power, ItemStack stack, boolean treasure, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        if (stack.getItem() instanceof BlindsightTongueWhipItem) {
            List<EnchantmentInstance> list = cir.getReturnValue();

            List<Enchantment> whipEnchantments = Arrays.asList(
                    Enchantments.KNOCKBACK,
                    Enchantments.FIRE_ASPECT,
                    ModEnchantments.LASHING.get(),
                    ModEnchantments.DISARMING.get(),
                    ModEnchantments.LEAPING.get(),
                    ModEnchantments.ILLUMINATING.get()
            );

            for (Enchantment enchantment : whipEnchantments) {
                boolean alreadyPresent = list.stream().anyMatch(e -> e.enchantment == enchantment);

                if (!alreadyPresent) {
                    for (int level = enchantment.getMinLevel(); level <= enchantment.getMaxLevel(); level++) {
                        if (power >= enchantment.getMinCost(level) && power <= enchantment.getMaxCost(level)) {
                            list.add(new EnchantmentInstance(enchantment, level));
                        }
                    }
                }
            }
        }
    }
}