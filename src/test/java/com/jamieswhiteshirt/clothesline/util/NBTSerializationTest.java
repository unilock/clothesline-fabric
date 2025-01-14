package com.jamieswhiteshirt.clothesline.util;

import com.jamieswhiteshirt.clothesline.common.util.BasicPersistentNetwork;
import com.jamieswhiteshirt.clothesline.common.util.NBTSerialization;
import net.minecraft.nbt.NbtCompound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NBTSerializationTest {
    @Test
    void persistsPersistentNetworkEquality() {
        BasicPersistentNetwork written = BasicPersistentNetwork.fromAbsolute(NetworkTests.ab.persistentNetwork);
        NbtCompound nbtTagCompound = NBTSerialization.writePersistentNetwork(written);
        BasicPersistentNetwork read = NBTSerialization.readPersistentNetwork(nbtTagCompound);
        Assertions.assertEquals(written, read);
    }
}
