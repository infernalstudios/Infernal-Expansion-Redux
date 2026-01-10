package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.mixin.accessor.ItemPropertiesAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItemProperties {
    public static void init() {
        makeBow(ModItems.GLOWSILK_BOW.get());
    }

    private static void makeBow(Item item) {
        ItemPropertiesAccessor.register(item, new ResourceLocation("pull"), (stack, level, entity, seed) -> {
            if (entity == null) {
                return 0.0F;
            } else {
                return entity.getUseItem() != stack ? 0.0F : (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks()) / 20.0F;
            }
        });

        ItemPropertiesAccessor.register(item, new ResourceLocation("pulling"), (stack, level, entity, seed) -> {
            return entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
        });
    }
}