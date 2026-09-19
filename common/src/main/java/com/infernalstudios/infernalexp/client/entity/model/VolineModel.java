package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.animation.VolineAnimation;
import com.infernalstudios.infernalexp.entities.VolineEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class VolineModel extends IEHierarchicalModel<VolineEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "voline"), "main");

    private final ModelPart root;

    public VolineModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition p_all = root.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));

        PartDefinition p_body = p_all.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0F, -4F, 4F));

        PartDefinition p_head = p_body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -8F, 8F, 8F, 8F), PartPose.offsetAndRotation(0F, 0F, 0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition p_mouth_roof = p_head.addOrReplaceChild("mouth_roof", CubeListBuilder.create().texOffs(0, 27).addBox(-4F, -4F, 1F, 4F, 4F, 0F).texOffs(8, 27).addBox(0F, -4F, 1F, 4F, 4F, 0F).texOffs(16, 27).addBox(0F, 0F, 1F, 4F, 4F, 0F).texOffs(24, 27).addBox(-4F, 0F, 1F, 4F, 4F, 0F), PartPose.offsetAndRotation(0F, -2F, -4F, -1.5708F, 0.0F, 0.0F));

        PartDefinition p_jaw = p_body.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 16).addBox(-4F, -5F, -4F, 8F, 3F, 8F), PartPose.offset(0F, 4F, -4F));

        PartDefinition p_mouth_floor = p_jaw.addOrReplaceChild("mouth_floor", CubeListBuilder.create().texOffs(0, 27).addBox(-4F, -4F, 1F, 4F, 4F, 0F).texOffs(8, 27).addBox(0F, -4F, 1F, 4F, 4F, 0F).texOffs(16, 27).addBox(0F, 0F, 1F, 4F, 4F, 0F).texOffs(24, 27).addBox(-4F, 0F, 1F, 4F, 4F, 0F), PartPose.offsetAndRotation(0F, -4F, 0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition p_frontrightleg = p_all.addOrReplaceChild("frontrightleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offset(-2F, -2F, -2F));

        PartDefinition p_frontleftleg = p_all.addOrReplaceChild("frontleftleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offset(2F, -2F, -2F));

        PartDefinition p_backrightleg = p_all.addOrReplaceChild("backrightleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offset(-2F, -2F, 2F));

        PartDefinition p_backleftleg = p_all.addOrReplaceChild("backleftleg", CubeListBuilder.create().texOffs(0, 0).addBox(-1F, 0F, -1F, 2F, 2F, 2F), PartPose.offset(2F, -2F, 2F));

        return LayerDefinition.create(mesh, 32, 32);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(VolineEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetPose();
        if (limbSwingAmount > 0.01F) {
            this.animateAt(VolineAnimation.WALK, ageInTicks);
        } else {
            this.animateAt(VolineAnimation.IDLE, ageInTicks);
        }

        this.animateOnce(entity.eatAnimationState, VolineAnimation.EAT, ageInTicks);
    }
}
