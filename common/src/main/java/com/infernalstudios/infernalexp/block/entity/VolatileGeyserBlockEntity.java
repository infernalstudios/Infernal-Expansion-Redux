package com.infernalstudios.infernalexp.block.entity;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.block.VolatileGeyserBlock;
import com.infernalstudios.infernalexp.module.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class VolatileGeyserBlockEntity extends BlockEntity implements GeoBlockEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int cooldown = 0;

    public VolatileGeyserBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.VOLATILE_GEYSER.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, VolatileGeyserBlockEntity entity) {
        boolean isPowered = state.getValue(VolatileGeyserBlock.POWERED);
        Direction facing = state.getValue(VolatileGeyserBlock.FACING);
        double range = IECommon.getConfig().common.geyserSteamHeight;

        if (isPowered) {
            if (level.isClientSide) {
                entity.spawnStreamParticles(level, pos, facing, range);
            } else {
                entity.applyStreamPhysics(level, pos, facing, range);
            }

            if (level.isClientSide && level.getGameTime() % 10 == 0) {
                level.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 0.5F + level.random.nextFloat() * 0.2F, false);
            }
        } else {
            if (!level.isClientSide) {
                if (entity.cooldown > 0) {
                    entity.cooldown--;
                } else {
                    entity.cooldown = 40 + level.random.nextInt(60);
                    level.blockEvent(pos, state.getBlock(), 1, 0);
                }
            }
        }
    }

    private void applyStreamPhysics(Level level, BlockPos pos, Direction facing, double range) {
        AABB effectBox = new AABB(pos).expandTowards(
                facing.getStepX() * range,
                facing.getStepY() * range,
                facing.getStepZ() * range
        );

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, effectBox);
        Vec3 directionVec = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());
        Vec3 centerPos = pos.getCenter();

        double gravity = 0.08;
        boolean isVertical = facing.getAxis().isVertical();

        for (Entity target : entities) {
            target.fallDistance = 0;

            if (target instanceof LivingEntity living && living.isFallFlying()) {
                Vec3 currentVel = target.getDeltaMovement();
                double currentSpeed = currentVel.dot(directionVec);

                if (currentSpeed < 1.5) {
                    Vec3 boost = directionVec.scale(0.15);
                    target.setDeltaMovement(currentVel.add(boost));
                    target.hurtMarked = true;
                }
                continue;
            }

            Vec3 distVec = target.position().subtract(centerPos);
            double distAlongAxis = distVec.dot(directionVec);

            if (distAlongAxis < 0 || distAlongAxis > range) continue;

            double distToTop = range - distAlongAxis;
            Vec3 currentVel = target.getDeltaMovement();
            double currentSpeedAlongAxis = currentVel.dot(directionVec);

            double softZone = 4.0;
            double powerScale = Math.min(distToTop, softZone) / softZone;

            double pushForce = 0.15 * powerScale;

            if (isVertical) {
                pushForce += gravity;

                if (currentSpeedAlongAxis > 0.5) {
                    pushForce *= 0.5;
                }
            }

            if (currentSpeedAlongAxis < 0.8) {
                target.setDeltaMovement(target.getDeltaMovement().add(directionVec.scale(pushForce)));
            }

            if (target instanceof LivingEntity) {
                Vec3 lateralVel = currentVel.subtract(directionVec.scale(currentSpeedAlongAxis));
                if (lateralVel.lengthSqr() > 0.0001) {
                    target.setDeltaMovement(target.getDeltaMovement().add(lateralVel.scale(0.05)));
                }
            }

            target.hurtMarked = true;
        }
    }

    private void applyBurstPhysics(Level level, BlockPos pos, Direction facing, double range) {
        AABB effectBox = new AABB(pos).expandTowards(
                facing.getStepX() * range,
                facing.getStepY() * range,
                facing.getStepZ() * range
        );

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, effectBox);
        Vec3 directionVec = new Vec3(facing.getStepX(), facing.getStepY(), facing.getStepZ());

        double gravity = 0.08;
        double baseVelocity = Math.sqrt(2 * gravity * range);

        double dragCompensation = 1.25;
        double launchVelocity = baseVelocity * dragCompensation;

        if (!facing.getAxis().isVertical()) {
            launchVelocity = Math.sqrt(2 * 0.25 * range) * 1.1;
        }

        for (Entity target : entities) {
            target.fallDistance = 0;

            Vec3 motion = directionVec.scale(launchVelocity);

            target.setDeltaMovement(motion);
            target.hurtMarked = true;
            target.hasImpulse = true;
        }
    }

    private void spawnStreamParticles(Level level, BlockPos pos, Direction facing, double range) {
        if (level.getGameTime() % 2 != 0) return;

        for (int i = 0; i < 3; i++) {
            double distance = level.random.nextDouble() * range;

            double x = pos.getX() + 0.5 + (facing.getStepX() * distance);
            double y = pos.getY() + 0.5 + (facing.getStepY() * distance);
            double z = pos.getZ() + 0.5 + (facing.getStepZ() * distance);

            double spread = 0.4;
            double rx = (level.random.nextDouble() - 0.5) * spread;
            double ry = (level.random.nextDouble() - 0.5) * spread;
            double rz = (level.random.nextDouble() - 0.5) * spread;

            double speed = 0.05;
            double vx = facing.getStepX() * speed;
            double vy = facing.getStepY() * speed;
            double vz = facing.getStepZ() * speed;

            level.addParticle(ParticleTypes.CLOUD, x + rx, y + ry, z + rz, vx, vy, vz);
        }
    }

    private void spawnBurstParticles(Level level, BlockPos pos, Direction facing) {
        int particleCount = 40;
        double speed = 0.6;

        for(int i = 0; i < particleCount; i++) {
            double x = pos.getX() + 0.5 + (facing.getStepX() * 0.5);
            double y = pos.getY() + 0.5 + (facing.getStepY() * 0.5);
            double z = pos.getZ() + 0.5 + (facing.getStepZ() * 0.5);

            double spread = 0.5;
            double rx = (level.random.nextDouble() - 0.5) * spread;
            double ry = (level.random.nextDouble() - 0.5) * spread;
            double rz = (level.random.nextDouble() - 0.5) * spread;

            double vx = facing.getStepX() * speed + (level.random.nextDouble() - 0.5) * 0.2;
            double vy = facing.getStepY() * speed + (level.random.nextDouble() - 0.5) * 0.2;
            double vz = facing.getStepZ() * speed + (level.random.nextDouble() - 0.5) * 0.2;

            level.addParticle(ParticleTypes.POOF, x + rx, y + ry, z + rz, vx, vy, vz);
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            BlockState state = this.getBlockState();
            Direction facing = state.getValue(VolatileGeyserBlock.FACING);
            double range = IECommon.getConfig().common.geyserSteamHeight;

            if (this.level != null && !this.level.isClientSide) {
                this.applyBurstPhysics(this.level, this.worldPosition, facing, range);
            }
            if (this.level != null && this.level.isClientSide) {
                this.spawnBurstParticles(this.level, this.worldPosition, facing);
                this.level.playLocalSound(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 1.0F, false);
            }
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // controllers.add(new AnimationController<>(this, "controller", 0, state -> state.setAndContinue(RawAnimation.begin().thenLoop("animation.volatile_geyser.idle"))));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}