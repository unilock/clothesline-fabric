package com.jamieswhiteshirt.clothesline.mixin.server.network;

import com.jamieswhiteshirt.clothesline.common.event.TrackEntityCallback;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public abstract class EntityTrackerEntryMixin {
	@Shadow
	private @Final Entity entity;

    @Inject(
        at = @At("TAIL"),
        method = "startTracking"
    )
    private void onStartedTrackingBy(ServerPlayerEntity entity, CallbackInfo ci) {
        TrackEntityCallback.START.invoker().accept(entity, this.entity);
    }
}
