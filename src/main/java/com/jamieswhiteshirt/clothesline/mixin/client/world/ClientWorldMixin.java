package com.jamieswhiteshirt.clothesline.mixin.client.world;

import com.jamieswhiteshirt.clothesline.api.NetworkCollection;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.client.SoundNetworkCollectionListener;
import com.jamieswhiteshirt.clothesline.client.impl.ClientNetworkManager;
import com.jamieswhiteshirt.clothesline.common.impl.NetworkCollectionImpl;
import com.jamieswhiteshirt.clothesline.internal.WorldExtension;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(ClientWorld.class)
public abstract class ClientWorldMixin extends World implements NetworkManagerProvider, WorldExtension {
    private static final Identifier SOUND_KEY = new Identifier("clothesline", "sound");

    private final NetworkCollection networkCollection = new NetworkCollectionImpl();
    private final NetworkManager networkManager = new ClientNetworkManager((ClientWorld)(Object) this, networkCollection);

    protected ClientWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimension, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long seed, int maxChainedNeighborUpdates) {
        super(properties, registryRef, dimension, profiler, isClient, debugWorld, seed, maxChainedNeighborUpdates);
    }

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void constructor(ClientPlayNetworkHandler networkHandler, ClientWorld.Properties properties, RegistryKey<World> registryRef, RegistryEntry<DimensionType> dimensionTypeEntry, int loadDistance, int simulationDistance, Supplier<Profiler> profiler, WorldRenderer worldRenderer, boolean debugWorld, long seed, CallbackInfo ci) {
        networkManager.getNetworks().addEventListener(SOUND_KEY, new SoundNetworkCollectionListener());
    }

    @Override
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public void clothesline$tick() {
        networkManager.update();
    }
}
