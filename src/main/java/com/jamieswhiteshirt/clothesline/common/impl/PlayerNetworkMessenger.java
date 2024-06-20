package com.jamieswhiteshirt.clothesline.common.impl;

import com.jamieswhiteshirt.clothesline.api.Network;
import com.jamieswhiteshirt.clothesline.common.network.MessageChannels;
import com.jamieswhiteshirt.clothesline.common.network.message.AddNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.RemoveNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.SetAttachmentMessage;
import com.jamieswhiteshirt.clothesline.common.network.message.UpdateNetworkMessage;
import com.jamieswhiteshirt.clothesline.common.util.BasicAttachment;
import com.jamieswhiteshirt.clothesline.common.util.BasicNetwork;
import com.jamieswhiteshirt.clothesline.internal.NetworkMessenger;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerNetworkMessenger implements NetworkMessenger<ServerPlayerEntity> {
    @Override
    public void addNetwork(ServerPlayerEntity watcher, Network network) {
        watcher.networkHandler.sendPacket(MessageChannels.ADD_NETWORK.createClientboundPacket(
            new AddNetworkMessage(BasicNetwork.fromAbsolute(network))
        ));
    }

    @Override
    public void removeNetwork(ServerPlayerEntity watcher, Network network) {
        watcher.networkHandler.sendPacket(MessageChannels.REMOVE_NETWORK.createClientboundPacket(
            new RemoveNetworkMessage(network.getId())
        ));
    }

    @Override
    public void setAttachment(ServerPlayerEntity watcher, Network network, int attachmentKey, ItemStack stack) {
        if (stack.isEmpty()) {
            watcher.networkHandler.sendPacket(MessageChannels.REMOVE_ATTACHMENT.createClientboundPacket(
                new RemoveAttachmentMessage(network.getId(), attachmentKey)
            ));
        } else {
            watcher.networkHandler.sendPacket(MessageChannels.SET_ATTACHMENT.createClientboundPacket(
                new SetAttachmentMessage(network.getId(), new BasicAttachment(attachmentKey, stack))
            ));
        }
    }

    @Override
    public void setShiftAndMomentum(ServerPlayerEntity watcher, Network network, int shift, int momentum) {
        watcher.networkHandler.sendPacket(MessageChannels.UPDATE_NETWORK.createClientboundPacket(
            new UpdateNetworkMessage(network.getId(), shift, momentum)
        ));
    }
}
