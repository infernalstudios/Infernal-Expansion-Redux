package com.infernalstudios.infernalexp.entities.ai;

import com.infernalstudios.infernalexp.entities.VolineEntity;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;
import java.util.List;

public class EatItemsGoal extends Goal {
    private final VolineEntity mob;
    private ItemEntity targetItem;
    private int eatAnimationTick;

    public EatItemsGoal(VolineEntity mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.mob.isSeekingShelter()) {
            return false;
        }

        List<ItemEntity> list = this.mob.level().getEntitiesOfClass(ItemEntity.class,
                this.mob.getBoundingBox().inflate(8.0D, 8.0D, 8.0D),
                item -> mob.wantsToEat(item.getItem()));

        if (!list.isEmpty()) {
            this.targetItem = list.get(0);
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        if (this.targetItem == null || !this.targetItem.isAlive()) {
            this.stop();
            return;
        }

        if (this.eatAnimationTick > 0) {
            this.eatAnimationTick--;

            this.faceItemInstantly();

            if (this.eatAnimationTick % 4 == 0) {
                this.mob.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);

                if (this.mob.level() instanceof ServerLevel serverLevel) {
                    ItemStack stack = this.mob.getItemInHand(InteractionHand.MAIN_HAND);
                    if (!stack.isEmpty()) {
                        double x = this.mob.getX() + this.mob.getLookAngle().x / 2.0D;
                        double y = this.mob.getY() + 0.5D;
                        double z = this.mob.getZ() + this.mob.getLookAngle().z / 2.0D;

                        serverLevel.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, stack),
                                x, y, z, 5, 0.1, 0.1, 0.1, 0.05);
                    }
                }
            }

            if (this.eatAnimationTick == 0) {
                ItemStack itemOnGround = this.targetItem.getItem();
                this.mob.ate(itemOnGround.split(1));

                if (itemOnGround.isEmpty()) {
                    this.targetItem.discard();
                } else {
                    this.targetItem.setItem(itemOnGround);
                }

                this.stop();
            }
            return;
        }

        this.mob.getLookControl().setLookAt(this.targetItem, 30.0F, 30.0F);
        this.mob.getNavigation().moveTo(this.targetItem, 1.2D);

        if (this.mob.distanceToSqr(this.targetItem) < 2.0D) {
            this.mob.playEatingAnimation();
            this.faceItemInstantly();

            ItemStack visualStack = this.targetItem.getItem().copy();
            visualStack.setCount(1);
            this.mob.setItemInHand(InteractionHand.MAIN_HAND, visualStack);

            this.eatAnimationTick = this.mob.isGrown() ? 6 : 12;
        }
    }

    private void faceItemInstantly() {
        if (this.targetItem != null) {
            double d0 = this.targetItem.getX() - this.mob.getX();
            double d2 = this.targetItem.getZ() - this.mob.getZ();
            float targetYaw = (float)(Math.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;

            this.mob.setYRot(targetYaw);
            this.mob.setYHeadRot(targetYaw);
            this.mob.setYBodyRot(targetYaw);
        }
    }

    @Override
    public void stop() {
        this.mob.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        this.eatAnimationTick = 0;
        super.stop();
    }
}