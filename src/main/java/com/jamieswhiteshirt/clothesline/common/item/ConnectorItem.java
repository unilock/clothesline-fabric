package com.jamieswhiteshirt.clothesline.common.item;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.internal.ConnectorHolder;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConnectorItem extends Item {
    private final ConnectorBehavior behavior;
    private final ThreadLocal<ItemUsageContext> toStore = new ThreadLocal<>();

    public ConnectorItem(Settings settings, ConnectorBehavior behavior) {
        super(settings);
        this.behavior = behavior;
    }

    public void stopActiveHandWithTo(LivingEntity player, ItemUsageContext ctx) {
        toStore.set(ctx);
        player.stopUsingItem();
        toStore.set(null);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity entity, int timeLeft) {
        ConnectorHolder connectorHolder = entity instanceof ConnectorHolder ? (ConnectorHolder)entity : null;
        if (connectorHolder != null) {
            ItemUsageContext from = connectorHolder.clothesline$getFrom();
            ItemUsageContext to = toStore.get();
            if (from != null && to != null) {
                behavior.connect(from, to);
            }
            applyConnectorState(connectorHolder, world, (PlayerEntity) entity, null);
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack var1) {
        return UseAction.NONE;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        PlayerEntity player = ctx.getPlayer();
        if (player == null) return ActionResult.PASS;

        if (behavior.canConnectFrom(ctx)) {
            Hand hand = ctx.getHand();
            ctx.getPlayer().setCurrentHand(hand);
            applyConnectorState((ConnectorHolder) player, ctx.getWorld(), player, ctx);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    private void applyConnectorState(ConnectorHolder connectorHolder, World world, PlayerEntity player, @Nullable ItemUsageContext ctx) {
        if (!world.isClient) {
            Packet<ClientPlayPacketListener> packet = Clothesline.createConnectorStatePacket(ctx, player);
            PlayerStream.watching(player).forEach(watcher -> ((ServerPlayerEntity) watcher).networkHandler.sendPacket(packet));
        }
        connectorHolder.clothesline$setFrom(ctx);
    }

    public interface ConnectorBehavior {
        boolean canConnectFrom(ItemUsageContext from);

        boolean connect(ItemUsageContext from, ItemUsageContext to);
    }
}
