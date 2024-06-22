package com.jamieswhiteshirt.clothesline.common.network.messagehandler;

import com.jamieswhiteshirt.clothesline.api.Utility;
import com.jamieswhiteshirt.clothesline.common.item.ConnectorItem;
import com.jamieswhiteshirt.clothesline.common.network.message.StopUsingItemOnMessage;
import net.fabricmc.fabric.api.network.PacketContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.math.BlockPos;

import java.util.function.BiConsumer;

public class StopUsingItemOnMessageHandler implements BiConsumer<PacketContext, StopUsingItemOnMessage> {
    @Override
    public void accept(PacketContext ctx, StopUsingItemOnMessage msg) {
        PlayerEntity player = ctx.getPlayer();
        BlockPos pos = msg.hitResult.getBlockPos();
        if (player.squaredDistanceTo(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) >= 64.0D) {
            return;
        }

        if (player.getActiveItem().getItem() instanceof ConnectorItem connectorItem) {
            if (Validation.canReachPos(player, Utility.midVec(pos))) {
                connectorItem.stopActiveHandWithTo(player, new ItemUsageContext(
                    player,
                    player.getActiveHand(),
                    msg.hitResult
                ));
            } else {
                player.stopUsingItem();
            }
        } else {
            player.stopUsingItem();
        }
    }
}
