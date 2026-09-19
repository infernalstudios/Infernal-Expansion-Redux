package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.animation.GlowsilkMothAnimation;
import com.infernalstudios.infernalexp.entities.GlowsilkMothEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GlowsilkMothModel extends IEHierarchicalModel<GlowsilkMothEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "glowsilk_moth"), "main");

    private final ModelPart root;

    public GlowsilkMothModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition p_all = root.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offsetAndRotation(-1F, -10F, 0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition p_body = p_all.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(1F, 0F, 0F));

        p_body.addOrReplaceChild("body_r1", CubeListBuilder.create().texOffs(56, 60).addBox(-1F, -5F, -1F, 2F, 2F, 2F), PartPose.offsetAndRotation(0F, 10F, 0F, 0.0F, 3.1416F, 0.0F));

        p_body.addOrReplaceChild("body_r2", CubeListBuilder.create().texOffs(48, 44).addBox(-2F, -17F, -2F, 4F, 12F, 4F), PartPose.offsetAndRotation(0F, 10F, 0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition p_legs1 = p_body.addOrReplaceChild("legs1", CubeListBuilder.create(), PartPose.offset(0F, -3F, -2F));

        p_legs1.addOrReplaceChild("legs1_r1", CubeListBuilder.create().texOffs(48, 60).addBox(-2F, 0F, -2F, 4F, 0F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition p_legs2 = p_body.addOrReplaceChild("legs2", CubeListBuilder.create(), PartPose.offset(0F, 0F, -2F));

        p_legs2.addOrReplaceChild("legs2_r1", CubeListBuilder.create().texOffs(48, 60).addBox(-2F, 0F, -2F, 4F, 0F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition p_legs3 = p_body.addOrReplaceChild("legs3", CubeListBuilder.create(), PartPose.offset(0F, 3F, -2F));

        p_legs3.addOrReplaceChild("legs3_r1", CubeListBuilder.create().texOffs(48, 60).addBox(-2F, 0F, -2F, 4F, 0F, 2F), PartPose.offsetAndRotation(0F, 0F, 0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition p_antenna = p_all.addOrReplaceChild("antenna", CubeListBuilder.create().mirror().texOffs(51, 40).addBox(-3F, -4F, 0F, 6F, 4F, 0F).mirror(false).mirror().texOffs(51, 36).addBox(-3F, -4F, 0.05F, 6F, 4F, 0F).mirror(false), PartPose.offset(1F, -7F, -1F));

        PartDefinition p_leftwing = p_all.addOrReplaceChild("leftwing", CubeListBuilder.create().mirror().texOffs(0, 41).addBox(0F, -14F, 0F, 13F, 23F, 0F).mirror(false).mirror().texOffs(0, 17).addBox(0F, -14F, 0.05F, 13F, 23F, 0F).mirror(false), PartPose.offset(3F, 1F, 0F));

        PartDefinition p_rightwing = p_all.addOrReplaceChild("rightwing", CubeListBuilder.create().texOffs(0, 41).addBox(-13F, -14F, 0F, 13F, 23F, 0F).texOffs(0, 17).addBox(-13F, -14F, 0.05F, 13F, 23F, 0F), PartPose.offset(-1F, 1F, 0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(GlowsilkMothEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetPose();
        this.animateAt(GlowsilkMothAnimation.WOBBLE, ageInTicks);
        this.animateAt(GlowsilkMothAnimation.FLY, ageInTicks);
    }
}
