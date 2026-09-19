package com.infernalstudios.infernalexp.client.entity.model;

import com.infernalstudios.infernalexp.IEConstants;
import com.infernalstudios.infernalexp.client.entity.animation.GlowsquitoAnimation;
import com.infernalstudios.infernalexp.entities.GlowsquitoEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GlowsquitoModel extends IEHierarchicalModel<GlowsquitoEntity> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            ResourceLocation.fromNamespaceAndPath(IEConstants.MOD_ID, "glowsquito"), "main");

    private final ModelPart root;

    public GlowsquitoModel(ModelPart root) {
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot().addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition p_all = root.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0F, 0F, 0F));

        PartDefinition p_Head = p_all.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(26, 19).addBox(-2F, -2.6F, -4F, 4F, 4F, 4F), PartPose.offset(0F, -7.4F, -4F));

        PartDefinition p_Stinger = p_Head.addOrReplaceChild("Stinger", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, -0.2242F, -9.6543F, 1F, 0F, 10F), PartPose.offsetAndRotation(0F, -1.1F, 0F, 0.3927F, 0.0F, 0.0F));

        PartDefinition p_Body = p_all.addOrReplaceChild("Body", CubeListBuilder.create(), PartPose.offsetAndRotation(0F, -7.5F, -4F, -0.1745F, 0.0F, 0.0F));

        PartDefinition p_Torso = p_Body.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 17).addBox(-3F, -2F, -3.136F, 6F, 5F, 6F), PartPose.offset(0F, -2.5F, 2.5F));

        PartDefinition p_WingsBoth = p_Body.addOrReplaceChild("WingsBoth", CubeListBuilder.create(), PartPose.offset(0F, -4.3F, 0.5F));

        PartDefinition p_RightWing = p_WingsBoth.addOrReplaceChild("RightWing", CubeListBuilder.create().texOffs(17, 0).addBox(-4.8918F, -0.2128F, -0.2127F, 5F, 0F, 9F), PartPose.offsetAndRotation(-2.3F, 0F, 0F, 0.6545F, -0.2182F, 0.0F));

        PartDefinition p_LeftWing = p_WingsBoth.addOrReplaceChild("LeftWing", CubeListBuilder.create().texOffs(27, 0).addBox(-0.1082F, -0.2128F, -0.2127F, 5F, 0F, 9F), PartPose.offsetAndRotation(2.3F, 0F, 0F, 0.6545F, 0.2182F, 0.0F));

        PartDefinition p_Butt = p_Body.addOrReplaceChild("Butt", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -4.2218F, -1.6213F, 8F, 7F, 10F), PartPose.offsetAndRotation(0F, -1.1782F, 6.1213F, -0.7854F, 0.0F, 0.0F));

        PartDefinition p_LeftLimbs = p_Body.addOrReplaceChild("LeftLimbs", CubeListBuilder.create(), PartPose.offset(3F, -0.5F, 1.5F));

        PartDefinition p_LeftArm = p_LeftLimbs.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(2, 0).addBox(0.01F, 0F, -0.5F, 0F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, -1F, 0.1745F, 0.0F, 0.0F));

        PartDefinition p_LeftLeg = p_LeftLimbs.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(6, 0).addBox(0.01F, 0F, -0.5F, 0F, 8F, 1F), PartPose.offsetAndRotation(0F, 0.5F, 1F, 0.1745F, 0.0F, 0.0F));

        PartDefinition p_RightLimbs = p_Body.addOrReplaceChild("RightLimbs", CubeListBuilder.create(), PartPose.offset(-3F, -0.5F, 1.5F));

        PartDefinition p_RightArm = p_RightLimbs.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(4, 0).addBox(-0.01F, 0F, -0.5F, 0F, 8F, 1F), PartPose.offsetAndRotation(0F, 0F, -1F, 0.1745F, 0.0F, 0.0F));

        PartDefinition p_RightLeg = p_RightLimbs.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 0).addBox(-0.01F, 0F, -0.5F, 0F, 8F, 1F), PartPose.offsetAndRotation(0F, 0.5F, 1F, 0.1745F, 0.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public @NotNull ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(GlowsquitoEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.resetPose();
        if (entity.isEating()) {
            this.animateAt(GlowsquitoAnimation.PERCHING, ageInTicks);
            this.animateAt(GlowsquitoAnimation.DRINKING, ageInTicks);
        } else {
            this.animateAt(GlowsquitoAnimation.FLYING_FLAPPING, ageInTicks);
            this.animateAt(GlowsquitoAnimation.FLYING_WOBBLING, ageInTicks);
            this.animateAt(GlowsquitoAnimation.FLYING_TILTING, ageInTicks);

            this.animateOnce(entity.drinkOnceAnimationState, GlowsquitoAnimation.DRINKING, ageInTicks);
        }
    }
}
