package com.jamieswhiteshirt.clothesline.mixin.client.network;

import com.jamieswhiteshirt.clothesline.common.item.ConnectorItem;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.encryption.PlayerPublicKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {
    public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile, @Nullable PlayerPublicKey publicKey) {
        super(world, profile, publicKey);
    }

    @Redirect(
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"
        ),
        method = "tickMovement"
    )
    private boolean redirectIsUsingItem(ClientPlayerEntity clientPlayerEntity) {
        return clientPlayerEntity.isUsingItem() && !(clientPlayerEntity.getActiveItem().getItem() instanceof ConnectorItem);
    }
}
