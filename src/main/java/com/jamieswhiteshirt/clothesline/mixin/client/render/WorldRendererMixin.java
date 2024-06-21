package com.jamieswhiteshirt.clothesline.mixin.client.render;

import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.client.raycast.NetworkRaycastHitEntity;
import com.jamieswhiteshirt.clothesline.client.render.ClotheslineRenderLayers;
import com.jamieswhiteshirt.clothesline.client.render.ClotheslineRenderer;
import com.jamieswhiteshirt.clothesline.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.internal.ConnectorHolder;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.profiler.Profiler;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
    @Shadow @Final private MinecraftClient client;
    @Shadow private ClientWorld world;
    @Unique private final ClotheslineRenderer clotheslineRenderer = new ClotheslineRenderer(MinecraftClient.getInstance());

    @Inject(
        at = @At(
            value = "INVOKE_STRING",
            target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
            args = "ldc=blockentities"
        ),
        method = "render"
    )
    private void renderClotheslines(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f projectionMatrix, CallbackInfo ci, @Local(ordinal = 0) double x, @Local(ordinal = 1) double y, @Local(ordinal = 2) double z, @Local Frustum frustum, @Local VertexConsumerProvider.Immediate immediate) {
        world.getProfiler().swap("clotheslines");

        NetworkManager manager = ((NetworkManagerProvider) world).getNetworkManager();
        boolean showDebugInfo = client.options.debugEnabled;

        matrices.push();
        matrices.translate(-x, -y, -z);

        clotheslineRenderer.render(matrices, immediate, world, manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), frustum, tickDelta);
        if (showDebugInfo) {
            clotheslineRenderer.debugRender(matrices, immediate, manager.getNetworks().getNodes(), manager.getNetworks().getEdges(), frustum, camera);
        }

        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        if (client.options.getPerspective().isFirstPerson() && entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.getActiveItem().getItem() == ClotheslineItems.CLOTHESLINE) {
                ConnectorHolder connector = (ConnectorHolder) playerEntity;
                ItemUsageContext from = connector.clothesline$getFrom();
                if (from != null) {
                    clotheslineRenderer.renderFirstPersonPlayerHeldClothesline(matrices, immediate, playerEntity, from.getBlockPos(), tickDelta);
                }
            }
        }

        matrices.pop();

        immediate.draw(RenderLayer.getSolid());
        immediate.draw(TexturedRenderLayers.getEntitySolid());
        immediate.draw(TexturedRenderLayers.getEntityCutout());
        immediate.draw(ClotheslineRenderLayers.getClothesline());
    }

    @Inject(
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
            shift = At.Shift.AFTER,
            ordinal = 1
        ),
        method = "render"
    )
    private void renderHighlight(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f positionMatrix, CallbackInfo ci, @Local Profiler profiler, @Local(ordinal = 0) double x, @Local(ordinal = 1) double y, @Local(ordinal = 2) double z, @Local VertexConsumerProvider.Immediate immediate) {
        HitResult hitResult = client.crosshairTarget;
        if (renderBlockOutline && hitResult != null && hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult) hitResult).getEntity() instanceof NetworkRaycastHitEntity entity) {
            profiler.swap("outline");
            VertexConsumer vertexConsumer = immediate.getBuffer(RenderLayer.getLines());
            matrices.push();
            matrices.translate(-x, -y, -z);
            entity.getHit().renderHighlight(clotheslineRenderer, matrices, vertexConsumer, 0.0F, 0.0F, 0.0F, 0.4F);
            matrices.pop();
        }
    }
}
