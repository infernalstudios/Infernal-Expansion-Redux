package com.infernalstudios.infernalexp.items;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.entities.GlowsilkArrowEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GlowsilkBowItem extends BowItem {

    public GlowsilkBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player playerEntity) {
            ItemStack itemStack = playerEntity.getProjectile(stack);

            int ticksUsed = this.getUseDuration(stack, playerEntity) - timeLeft;
            if (ticksUsed < 0) return;

            float velocity = getPowerForTime(ticksUsed);
            if (velocity < 0.1D) return;

            if (itemStack.isEmpty()) {
                itemStack = new ItemStack(Items.ARROW);
            }

            if (!level.isClientSide) {
                GlowsilkArrowEntity abstractArrow = new GlowsilkArrowEntity(level, playerEntity, itemStack, stack);

                double speedMultiplier = IECommon.getConfig().common.miscellaneous.glowsilkBowSpeed;
                abstractArrow.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, velocity * 6.0F * (float) speedMultiplier, 1.0F);

                abstractArrow.setBaseDamage(abstractArrow.getBaseDamage() / 2.0D);

                if (velocity == 1.0F) {
                    abstractArrow.setCritArrow(true);
                }

                stack.hurtAndBreak(1, playerEntity, LivingEntity.getSlotForHand(playerEntity.getUsedItemHand()));

                if (playerEntity.getAbilities().instabuild) {
                    abstractArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }

                level.addFreshEntity(abstractArrow);
            }

            level.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

            if (!playerEntity.getAbilities().instabuild) {
                itemStack.shrink(1);
                if (itemStack.isEmpty()) {
                    playerEntity.getInventory().removeItem(itemStack);
                }
            }

            playerEntity.awardStat(Stats.ITEM_USED.get(this));
        }
    }
}