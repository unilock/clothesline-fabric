package com.jamieswhiteshirt.clothesline.common;

import com.jamieswhiteshirt.clothesline.Clothesline;
import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import com.jamieswhiteshirt.clothesline.internal.NetworkProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.world.PersistentState;

import java.util.stream.Collectors;

public class NetworkProviderPersistentState extends PersistentState {
    private static final int CURRENT_VERSION = 0;

    private final NetworkProvider provider;
    private int version;

    public NetworkProviderPersistentState(NetworkProvider provider) {
        super();
        this.provider = provider;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public static NetworkProviderPersistentState fromNbt(NbtCompound tag, NetworkProvider provider) {
        NetworkProviderPersistentState instance = new NetworkProviderPersistentState(provider);

        int version;
        if (!tag.contains("Version", NbtElement.INT_TYPE)) {
            version = CURRENT_VERSION;
        } else {
            version = tag.getInt("Version");
        }

        if (version > CURRENT_VERSION) {
            Clothesline.LOGGER.error("Invalid save data. Expected Version <= " + CURRENT_VERSION + ", found " + version + ". Discarding save data.");
            return instance;
        }

        instance.version = version;

        if (!tag.contains("Networks", NbtElement.LIST_TYPE)) {
            Clothesline.LOGGER.error("Invalid save data. Expected list of Networks, found none. Discarding save data.");
            return instance;
        }

        instance.provider.reset(
            NBTSerialization.readPersistentNetworks(tag.getList("Networks", NbtElement.COMPOUND_TYPE)).stream()
                .map(BasicPersistentNetwork::toAbsolute)
                .collect(Collectors.toList())
        );

        return instance;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("Version", this.version);
        tag.put("Networks", NBTSerialization.writePersistentNetworks(
            this.provider.getNetworks().stream()
                .map(BasicPersistentNetwork::fromAbsolute)
                .collect(Collectors.toList())
        ));
        return tag;
    }
}
