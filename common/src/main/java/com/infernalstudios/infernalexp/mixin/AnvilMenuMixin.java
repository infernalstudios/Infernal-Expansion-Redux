package com.infernalstudios.infernalexp.mixin;

import com.infernalstudios.infernalexp.module.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow
    @Final
    private DataSlot cost;

    @Shadow
    private int repairItemCountCost;

    @Shadow
    private String itemName;

    public AnvilMenuMixin(@Nullable MenuType<?> $$0, int $$1, Inventory $$2, ContainerLevelAccess $$3) {
        super($$0, $$1, $$2, $$3);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    public void glowsilkRepair(CallbackInfo ci) {
        ItemStack stack = this.inputSlots.getItem(0);
        if (!stack.isEmpty() && stack.getDamageValue() > 0 && this.inputSlots.getItem(1).is(ModItems.GLOWSILK_STRING.get())) {
            int dura = Math.min((stack.getDamageValue() + 199) / 200, this.inputSlots.getItem(1).getCount());
            ItemStack output = stack.copy();
            output.setDamageValue(output.getDamageValue() - 200 * dura);

            this.repairItemCountCost = dura;

            if (this.itemName != null && !this.itemName.isBlank()) {
                if (!this.itemName.equals(stack.getHoverName().getString())) {
                    dura += 1;
                    output.set(DataComponents.CUSTOM_NAME, Component.literal(this.itemName));
                }
            } else if (stack.has(DataComponents.CUSTOM_NAME)) {
                dura += 1;
                output.remove(DataComponents.CUSTOM_NAME);
            }

            this.cost.set(dura * 5);

            this.resultSlots.setItem(0, output);
            ci.cancel();
        }
    }
}
