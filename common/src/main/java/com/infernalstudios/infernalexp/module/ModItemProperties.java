package com.infernalstudios.infernalexp.module;

import com.infernalstudios.infernalexp.items.BlindsightTongueWhipItem;
import com.infernalstudios.infernalexp.mixin.accessor.ItemPropertiesAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ModItemProperties {
    public static void init() {
        makeBow(ModItems.GLOWSILK_BOW.get());
        makeWhip(ModItems.BLINDSIGHT_TONGUE_WHIP.get());
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

    private static void makeWhip(Item item) {
        ItemPropertiesAccessor.register(item, new ResourceLocation("infernalexp", "whip_progress"), (stack, level, entity, seed) -> {
            if (entity != null && entity.isUsingItem() && entity.getUseItem() == stack) {
                float useTime = (float) (stack.getUseDuration() - entity.getUseItemRemainingTicks());
                return Math.min(1.0F, useTime / BlindsightTongueWhipItem.CHARGE_CAP_TICKS);
            }

            Level world = level != null ? level : (entity != null ? entity.level() : null);
            if (world == null) return 0.0F;

            long start = 0;
            long time = world.getGameTime();

            if (entity != null && BlindsightTongueWhipItem.CLIENT_ATTACK_TIMES.containsKey(entity.getId())) {
                long mappedStart = BlindsightTongueWhipItem.CLIENT_ATTACK_TIMES.get(entity.getId());
                if (time - mappedStart >= 0 && time - mappedStart <= BlindsightTongueWhipItem.ATTACK_DURATION_TICKS) {
                    start = mappedStart;
                }
            }

            if (start == 0 && BlindsightTongueWhipItem.isAttacking(stack)) {
                start = BlindsightTongueWhipItem.getAttackStartTick(stack);
            }

            if (start != 0) {
                float elapsed = (float) (time - start);
                if (elapsed <= BlindsightTongueWhipItem.ATTACK_DURATION_TICKS) {
                    return 1.0F + (elapsed / BlindsightTongueWhipItem.ATTACK_DURATION_TICKS);
                }
            }

            return 0.0F;
        });
    }
}