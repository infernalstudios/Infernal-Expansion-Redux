package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.animation.VolineBigAnimation;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VolineBigModel extends IEHierarchicalModel<VolineEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "voline_big"), "main");

    private final ModelPart root;

    public VolineBigModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition p_all = root.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));

        PartDefinition p_head = p_all.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0F, -2F, 0F));

        PartDefinition p_skull = p_head.addOrReplaceChild("skull", CubeListBuilder.create().texOffs(0, 0).addBox(-5F, -8.8F, -10F, 10F, 12F, 10F).texOffs(0, 37).addBox(-5F, -1.7F, -10F, 10F, 2F, 10F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0F, -3.2F, 5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition p_jaw = p_head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 23).addBox(-5F, 0F, -10F, 10F, 3F, 10F, new CubeDeformation(0.1F)).texOffs(0, 37).addBox(-5F, 1F, -10F, 10F, 2F, 10F, new CubeDeformation(-0.1F)), PartPose.offset(0F, -3.1F, 5F));

        p_jaw.addOrReplaceChild("jaw_r1", CubeListBuilder.create().texOffs(0, 61).addBox(-5F, -3F, 0F, 10F, 3F, 0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0F, 2.9F, -0.3F, -0.0436F, 0.0F, 0.0F));

        PartDefinition p_legs = p_all.addOrReplaceChild("legs", CubeListBuilder.create(), PartPose.offset(0F, -4F, 0F));

        PartDefinition p_frontleftleg = p_all.addOrReplaceChild("frontleftleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 2F, -1F, 2F, 3F, 2F), PartPose.offset(3F, -5F, -3F));

        PartDefinition p_backleftleg = p_all.addOrReplaceChild("backleftleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 1F, -1F, 2F, 3F, 2F), PartPose.offset(3F, -4F, 3F));

        PartDefinition p_frontrightleg = p_all.addOrReplaceChild("frontrightleg", CubeListBuilder.create().mirror().texOffs(0, 0).addBox(-1F, 2F, -1F, 2F, 3F, 2F).mirror(false), PartPose.offset(-3F, -5F, -3F));

        PartDefinition p_backrightleg = p_all.addOrReplaceChild("backrightleg", CubeListBuilder.create().mirror().texOffs(0, 0).addBox(-1F, 1F, -1F, 2F, 3F, 2F).mirror(false), PartPose.offset(-3F, -4F, 3F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(VolineEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetPose();
        if (entity.isSleeping()) {
            int asleepFor = VolineEntity.SLEEP_DURATION_TICKS - entity.getSleepTimer();

            if (asleepFor < 40) {
                this.animateAt(VolineBigAnimation.FALLING_ASLEEP, asleepFor);
            } else {
                this.animateAt(VolineBigAnimation.ASLEEP, ageInTicks);
            }
        } else if (limbSwingAmount > 0.01F) {
            this.animateAt(VolineBigAnimation.WALK, ageInTicks);
        } else {
            this.animateAt(VolineBigAnimation.IDLE, ageInTicks);
        }

        this.animateOnce(entity.eatAnimationState, VolineBigAnimation.EAT, ageInTicks);
    }
}
