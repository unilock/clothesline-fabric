package com.jamieswhiteshirt.clothesline.mixin.client.util.math;

import com.jamieswhiteshirt.clothesline.client.Matrix3fExtension;
import org.joml.Matrix3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Matrix3f.class, remap = false)
public class Matrix3fMixin implements Matrix3fExtension {
    @Shadow public float m00;
    @Shadow public float m01;
    @Shadow public float m02;
    @Shadow public float m10;
    @Shadow public float m11;
    @Shadow public float m12;
    @Shadow public float m20;
    @Shadow public float m21;
    @Shadow public float m22;

    @Override
    public void load(
        float a00, float a01, float a02,
        float a10, float a11, float a12,
        float a20, float a21, float a22
    ) {
        this.m00 = a00; this.m01 = a01; this.m02 = a02;
        this.m10 = a10; this.m11 = a11; this.m12 = a12;
        this.m20 = a20; this.m21 = a21; this.m22 = a22;
    }
}
