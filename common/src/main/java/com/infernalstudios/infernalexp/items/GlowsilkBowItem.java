package com.infernalstudios.infernalexp.items;

import com.infernalstudios.infernalexp.api.AbstractArrowEntityAccess;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GlowsilkBowItem extends BowItem {

    public GlowsilkBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player playerEntity) {
            boolean hasInfinity = playerEntity.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack itemStack = playerEntity.getProjectile(stack);

            int ticksUsed = this.getUseDuration(stack) - timeLeft;
            if (ticksUsed < 0) return;

            if (!itemStack.isEmpty() || hasInfinity) {
                if (itemStack.isEmpty()) {
                    itemStack = new ItemStack(Items.ARROW);
                }

                float velocity = getPowerForTime(ticksUsed);
                if (!((double) velocity < 0.1D)) {
                    boolean isArrowInfinite = playerEntity.getAbilities().instabuild || (itemStack.is(Items.ARROW) && hasInfinity);

                    if (!level.isClientSide) {
                        ArrowItem arrowItem = (ArrowItem) (itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW);
                        AbstractArrow abstractArrow = arrowItem.createArrow(level, itemStack, playerEntity);

                        if (abstractArrow instanceof AbstractArrowEntityAccess access) {
                            access.infernalexp$setGlow(true);
                            access.infernalexp$setLuminous(true);
                        }

                        abstractArrow.shootFromRotation(playerEntity, playerEntity.getXRot(), playerEntity.getYRot(), 0.0F, velocity * 6.0F, 1.0F);

                        abstractArrow.setBaseDamage((abstractArrow.getBaseDamage() / 2.0D) + 0.1D);

                        if (velocity == 1.0F) {
                            abstractArrow.setCritArrow(true);
                        }

                        int power = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (power > 0) {
                            abstractArrow.setBaseDamage(abstractArrow.getBaseDamage() + (double) power * 0.5D + 0.5D);
                        }

                        int knockback = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (knockback > 0) {
                            abstractArrow.setKnockback(knockback);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0) {
                            abstractArrow.setSecondsOnFire(100);
                        }

                        stack.hurtAndBreak(1, playerEntity, (player) -> player.broadcastBreakEvent(playerEntity.getUsedItemHand()));

                        if (isArrowInfinite || playerEntity.getAbilities().instabuild && (itemStack.is(Items.SPECTRAL_ARROW) || itemStack.is(Items.TIPPED_ARROW))) {
                            abstractArrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        }

                        level.addFreshEntity(abstractArrow);
                    }

                    level.playSound(null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (level.getRandom().nextFloat() * 0.4F + 1.2F) + velocity * 0.5F);

                    if (!isArrowInfinite && !playerEntity.getAbilities().instabuild) {
                        itemStack.shrink(1);
                        if (itemStack.isEmpty()) {
                            playerEntity.getInventory().removeItem(itemStack);
                        }
                    }

                    playerEntity.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }
}