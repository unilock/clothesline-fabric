package com.jamieswhiteshirt.clothesline.client.raycast;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.api.NetworkEdge;
import com.jamieswhiteshirt.clothesline.client.render.ClotheslineRenderer;
import com.jamieswhiteshirt.clothesline.common.item.ClotheslineItems;
import com.jamieswhiteshirt.clothesline.common.network.MessageChannels;
import com.jamieswhiteshirt.clothesline.common.network.message.HitNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.TryUseItemOnNetworkMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class EdgeRaycastHit extends NetworkRaycastHit {
    public final double offset;

    public EdgeRaycastHit(double distanceSq, NetworkEdge edge, double offset) {
        super(distanceSq, edge);
        this.offset = offset;
    }

    @Override
    public boolean hitByEntity(PlayerEntity player) {
        int offset = (int) Math.round(this.offset);
        Network network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        Vec3d pos = edge.getPathEdge().getPositionForOffset(offset);
        player.getWorld().playSound(player, pos.x, pos.y, pos.z, SoundEvents.ENTITY_LEASH_KNOT_BREAK, SoundCategory.BLOCKS, 1.0F, 1.0F);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(MessageChannels.HIT_NETWORK.createServerboundPacket(
            new HitNetworkMessage(network.getId(), attachmentKey, offset)
        ));
        return true;
    }

    @Override
    public ActionResult useItem(PlayerEntity player, Hand hand) {
        int offset = (int) Math.round(this.offset);
        Network network = edge.getNetwork();
        int attachmentKey = network.getState().offsetToAttachmentKey(offset);
        MinecraftClient.getInstance().getNetworkHandler().sendPacket(MessageChannels.TRY_USE_ITEM_ON_NETWORK.createServerboundPacket(
            new TryUseItemOnNetworkMessage(hand, network.getId(), attachmentKey)
        ));
        return network.useItem(player, hand, attachmentKey);
    }

    @Override
    public void renderHighlight(ClotheslineRenderer clotheslineRenderer, MatrixStack matrices, VertexConsumer vertices, float r, float g, float b, float a) {
        clotheslineRenderer.renderOutline(matrices, vertices, edge, r, g, b, a);
    }

    @Override
    public ItemStack getPickedResult() {
        return new ItemStack(ClotheslineItems.CLOTHESLINE);
    }

    @Override
    public String getDebugString() {
        return "Position: " + Math.round(offset);
    }
}
