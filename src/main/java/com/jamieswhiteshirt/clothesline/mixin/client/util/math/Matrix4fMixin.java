package com.jamieswhiteshirt.clothesline.mixin.client.util.math;

import com.jamieswhiteshirt.clothesline.client.Matrix4fExtension;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = Matrix4f.class, remap = false)
public class Matrix4fMixin implements Matrix4fExtension {
    @Shadow float m00;
    @Shadow float m01;
    @Shadow float m02;
    @Shadow float m03;
    @Shadow float m10;
    @Shadow float m11;
    @Shadow float m12;
    @Shadow float m13;
    @Shadow float m20;
    @Shadow float m21;
    @Shadow float m22;
    @Shadow float m23;
    @Shadow float m30;
    @Shadow float m31;
    @Shadow float m32;
    @Shadow float m33;

    @Override
    public void load(
        float a00, float a01, float a02, float a03,
        float a10, float a11, float a12, float a13,
        float a20, float a21, float a22, float a23,
        float a30, float a31, float a32, float a33
    ) {
        this.m00 = a00; this.m01 = a01; this.m02 = a02; this.m03 = a03;
        this.m10 = a10; this.m11 = a11; this.m12 = a12; this.m13 = a13;
        this.m20 = a20; this.m21 = a21; this.m22 = a22; this.m23 = a23;
        this.m30 = a30; this.m31 = a31; this.m32 = a32; this.m33 = a33;
    }
}
