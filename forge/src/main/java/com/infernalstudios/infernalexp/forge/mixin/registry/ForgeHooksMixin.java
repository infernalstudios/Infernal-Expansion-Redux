package com.infernalstudios.infernalexp.forge.mixin.registry;

import com.infernalstudios.infernalexp.registration.FuelRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeHooks.class)
public class ForgeHooksMixin {

    @Inject(method = "getBurnTime", at = @At("RETURN"), cancellable = true, remap = false)
    private static void infernalexp$getRegistryBurnTimes(ItemStack stack, @Nullable RecipeType<?> recipeType, CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() > 0) return;

        cir.setReturnValue(FuelRegistry.getCookTime(stack.getItem()));
    }
}
