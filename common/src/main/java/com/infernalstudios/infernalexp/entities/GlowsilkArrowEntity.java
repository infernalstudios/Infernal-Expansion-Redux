package com.infernalstudios.infernalexp.entities;

import com.infernalstudios.infernalexp.api.AbstractArrowEntityAccess;
import com.infernalstudios.infernalexp.module.ModEntityTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class GlowsilkArrowEntity extends Arrow {

    public GlowsilkArrowEntity(EntityType<? extends Arrow> type, Level level) {
        super(type, level);
    }

    public GlowsilkArrowEntity(Level level, LivingEntity shooter) {
        super(level, shooter);
        if (this instanceof AbstractArrowEntityAccess access) {
            access.infernalexp$setGlow(true);
            access.infernalexp$setLuminous(true);
        }
    }

    @Override
    public @NotNull EntityType<?> getType() {
        return ModEntityTypes.GLOWSILK_ARROW.get();
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }
}