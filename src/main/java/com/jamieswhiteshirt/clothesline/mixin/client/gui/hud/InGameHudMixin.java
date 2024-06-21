package com.jamieswhiteshirt.clothesline.mixin.client.gui.hud;

import com.jamieswhiteshirt.clothesline.api.client.RichBlockInteraction;
import com.jamieswhiteshirt.clothesline.api.client.RichInteractionType;
import com.jamieswhiteshirt.clothesline.client.ClotheslineClient;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Unique private static final Identifier CLOTHESLINE_GUI_ICONS = new Identifier("clothesline", "textures/gui/icons.png");
    @Unique private static final int CLOTHESLINE_ICONS_WIDTH = 32, CLOTHESLINE_ICONS_HEIGHT = 16;

    @Unique
    private static void drawTexture(MatrixStack matrices, float x, float y, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        float uScale = 1.0F / textureWidth;
        float vScale = 1.0F / textureHeight;
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix, x, y + regionHeight, 0.0F).texture(u * uScale, (v + regionHeight) * vScale).next();
        bufferBuilder.vertex(matrix, x + regionWidth, y + regionHeight, 0.0F).texture((u + regionWidth) * uScale, (v + regionHeight) * vScale).next();
        bufferBuilder.vertex(matrix, x + regionWidth, y, 0.0F).texture((u + regionWidth) * uScale, v * vScale).next();
        bufferBuilder.vertex(matrix, x, y, 0.0F).texture(u * uScale, v * vScale).next();
		tessellator.draw();
    }

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private static Identifier ICONS;

    @Shadow protected abstract PlayerEntity getCameraPlayer();

    @Inject(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V",
            ordinal = 0
        ),
        method = "renderCrosshair"
    )
    private void renderCrosshair(DrawContext context, CallbackInfo ci) {
        PlayerEntity player = getCameraPlayer();
        HitResult hitResult = client.crosshairTarget;
        RichInteractionType richInteractionType = RichInteractionType.NONE;
        if (player != null && hitResult != null) {
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                BlockState state = client.world.getBlockState(pos);
                RichBlockInteraction richInteraction = ClotheslineClient.richInteractionRegistry.getBlock(state.getBlock());
                if (richInteraction != null) {
                    richInteractionType = richInteraction.getRichInteractionType(state, client.world, pos, player, (BlockHitResult) hitResult);
                }
            }
        }

        switch (richInteractionType) {
            case ROTATE_CLOCKWISE:
            	RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
                RenderSystem.setShaderTexture(0, CLOTHESLINE_GUI_ICONS);
                drawTexture(context.getMatrices(), scaledWidth / 2.0F - 16F, scaledHeight / 2.0F - 7.5F, 0.0F, 0.0F, 15, 15, CLOTHESLINE_ICONS_WIDTH, CLOTHESLINE_ICONS_HEIGHT);
                RenderSystem.setShaderTexture(0, ICONS);
                break;
            case ROTATE_COUNTER_CLOCKWISE:
            	RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
            	RenderSystem.setShaderTexture(0, CLOTHESLINE_GUI_ICONS);
                drawTexture(context.getMatrices(), scaledWidth / 2.0F, scaledHeight / 2.0F - 7.5F, 16.0F, 0.0F, 15, 15, CLOTHESLINE_ICONS_WIDTH, CLOTHESLINE_ICONS_HEIGHT);
                RenderSystem.setShaderTexture(0, ICONS);
                break;
        }
    }
}
