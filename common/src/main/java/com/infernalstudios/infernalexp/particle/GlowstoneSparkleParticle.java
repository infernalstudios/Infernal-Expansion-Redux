package com.infernalstudios.infernalexp.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class GlowstoneSparkleParticle extends TextureSheetParticle {
    public GlowstoneSparkleParticle(ClientLevel clientWorld, double x, double y, double z) {
        super(clientWorld, x, y, z);
    }

    public GlowstoneSparkleParticle(ClientLevel clientWorld, double x, double y, double z, TextureAtlasSprite sprite, float vx, float vy, float vz) {
        super(clientWorld, x, y, z);

        this.setSprite(sprite);

        this.setPos(x, y, z);
        this.setParticleSpeed(vx, vy, vz);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }
}
