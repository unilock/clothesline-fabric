package com.jamieswhiteshirt.clothesline.mixin.server.world;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import com.jamieswhiteshirt.clothesline.api.NetworkCollection;
import com.jamieswhiteshirt.clothesline.api.NetworkManager;
import com.jamieswhiteshirt.clothesline.api.NetworkManagerProvider;
import com.jamieswhiteshirt.clothesline.common.NetworkProviderPersistentState;
import com.jamieswhiteshirt.clothesline.common.impl.*;
import com.jamieswhiteshirt.clothesline.internal.NetworkCollectionTracker;
import com.jamieswhiteshirt.clothesline.internal.NetworkProvider;
import com.jamieswhiteshirt.clothesline.internal.ServerWorldExtension;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin extends World implements NetworkManagerProvider, ServerWorldExtension {
    @Unique private static final String PERSISTENT_STATE_KEY = "clothesline_provider";

    @Unique private final SetMultimap<ChunkPos, ServerPlayerEntity> chunkWatchers = MultimapBuilder.hashKeys().linkedHashSetValues().build();
    @Unique private final NetworkCollection networkCollection = new NetworkCollectionImpl();
    @Unique private final NetworkProvider networkProvider = new NetworkProviderImpl(networkCollection, pos -> isChunkLoaded(ChunkPos.getPackedX(pos), ChunkPos.getPackedZ(pos)));
    @Unique private final NetworkManager networkManager = new ServerNetworkManager((ServerWorld)(Object) this, networkCollection, networkProvider);
    @Unique private final NetworkCollectionTracker<ServerPlayerEntity> tracker = new NetworkCollectionTrackerImpl<>(networkCollection, chunkWatchers::get, new PlayerNetworkMessenger());

    protected ServerWorldMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(
        method = "<init>",
        at = @At("TAIL")
    )
    private void constructor(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        PersistentStateManager persistentStateManager = ((ServerWorld) (Object) this).getPersistentStateManager();
        persistentStateManager.getOrCreate((nbtCompound) -> NetworkProviderPersistentState.readNbt(nbtCompound, networkProvider), () -> new NetworkProviderPersistentState(networkProvider), PERSISTENT_STATE_KEY);
    }

    @Override
    public NetworkManager getNetworkManager() {
        return networkManager;
    }

    @Override
    public void clothesline$tick() {
        networkManager.update();
        tracker.update();
    }

    @Override
    public void clothesline$onPlayerWatchChunk(ChunkPos pos, ServerPlayerEntity player) {
        chunkWatchers.put(pos, player);
        tracker.onWatchChunk(player, pos);
    }

    @Override
    public void clothesline$onPlayerUnWatchChunk(ChunkPos pos, ServerPlayerEntity player) {
        chunkWatchers.remove(pos, player);
        tracker.onUnWatchChunk(player, pos);
    }

    @Override
    public void clothesline$onChunkLoaded(ChunkPos pos) {
        networkProvider.onChunkLoaded(pos);
    }

    @Override
    public void clothesline$onChunkUnloaded(ChunkPos pos) {
        networkProvider.onChunkUnloaded(pos);
    }
}
