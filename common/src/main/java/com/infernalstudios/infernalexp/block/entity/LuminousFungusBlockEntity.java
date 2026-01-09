package com.infernalstudios.infernalexp.block.entity;

import com.infernalstudios.infernalexp.IECommon;
import com.infernalstudios.infernalexp.block.LuminousFungusBlock;
import com.infernalstudios.infernalexp.module.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class LuminousFungusBlockEntity extends BlockEntity {
    private int lightTime = 0;

    public LuminousFungusBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntityTypes.LUMINOUS_FUNGUS.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, LuminousFungusBlockEntity entity) {
        if (level.getGameTime() % 5 != 0) {
            if (entity.lightTime > 0) entity.lightTime--;
            return;
        }

        double distance = IECommon.getConfig().common.miscellaneous.LuminousFungusActivateDistance;
        AABB area = new AABB(pos).inflate(distance);
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, area);

        if (!entities.isEmpty()) {
            entity.lightTime = 60;
            if (!state.getValue(LuminousFungusBlock.LIT)) {
                level.setBlock(pos, state.setValue(LuminousFungusBlock.LIT, true), 3);
            }
        } else {
            if (entity.lightTime > 0) {
                entity.lightTime -= 5;
            } else if (state.getValue(LuminousFungusBlock.LIT)) {
                level.setBlock(pos, state.setValue(LuminousFungusBlock.LIT, false), 3);
            }
        }
    }
}