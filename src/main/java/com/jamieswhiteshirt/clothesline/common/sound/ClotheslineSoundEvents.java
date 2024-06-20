package com.jamieswhiteshirt.clothesline.common.sound;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ClotheslineSoundEvents {
    public static final SoundEvent BLOCK_CLOTHESLINE_ANCHOR_SQUEAK = register("block.clothesline_anchor.squeak");
    public static final SoundEvent BLOCK_CLOTHESLINE_ANCHOR_ROPE = register("block.clothesline_anchor.rope");

    private static SoundEvent register(String id) {
        Identifier identifier = new Identifier("clothesline", id);
        return Registry.register(Registries.SOUND_EVENT, identifier, SoundEvent.of(identifier));
    }

    public static void init() { }
}
