package com.jamieswhiteshirt.clothesline.common.util;

import net.minecraft.util.math.MathHelper;
import org.joml.*;

import java.lang.Math;

public class JomlUtil {
    public static final Vector3f POSITIVE_X = new Vector3f(1.0F, 0.0F, 0.0F);
    public static final Vector3f POSITIVE_Y = new Vector3f(0.0F, 1.0F, 0.0F);

    public static Quaternionf getDegreesQuaternion(Vector3f axis, float rotationAngle) {
        rotationAngle *= (float) (Math.PI / 180.0);

        float f = MathHelper.sin(rotationAngle / 2.0F);
        return new Quaternionf(
                axis.x * f,
                axis.y * f,
                axis.z * f,
                MathHelper.cos(rotationAngle / 2.0F)
        );
    }

    public static Matrix3f quaternionToMatrix3f(Quaternionf quaternion) {
        Matrix3f matrix3f = new Matrix3f();
        float f = quaternion.x;
        float g = quaternion.y;
        float h = quaternion.z;
        float i = quaternion.w;
        float j = 2.0F * f * f;
        float k = 2.0F * g * g;
        float l = 2.0F * h * h;
       matrix3f.m00(1.0F - k - l);
       matrix3f.m11(1.0F - l - j);
       matrix3f.m22(1.0F - j - k);
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
       matrix3f.m10(2.0F * (m + r));
       matrix3f.m01(2.0F * (m - r));
       matrix3f.m20(2.0F * (o - q));
       matrix3f.m02(2.0F * (o + q));
       matrix3f.m21(2.0F * (n + p));
       matrix3f.m12(2.0F * (n - p));
        return matrix3f;
    }

    public static Matrix4f quaternionToMatrix4f(Quaternionf quaternion) {
        Matrix4f matrix4f = new Matrix4f();
        float f = quaternion.x;
        float g = quaternion.y;
        float h = quaternion.z;
        float i = quaternion.w;
        float j = 2.0F * f * f;
        float k = 2.0F * g * g;
        float l = 2.0F * h * h;
       matrix4f.m00(1.0F - k - l);
       matrix4f.m11(1.0F - l - j);
       matrix4f.m22(1.0F - j - k);
       matrix4f.m33(1.0F);
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
       matrix4f.m10(2.0F * (m + r));
       matrix4f.m01(2.0F * (m - r));
       matrix4f.m20(2.0F * (o - q));
       matrix4f.m02(2.0F * (o + q));
       matrix4f.m21(2.0F * (n + p));
       matrix4f.m12(2.0F * (n - p));
        return matrix4f;
    }
    
    public static void matrix3fMultiply(Matrix3f left, Matrix3f right) {
        float f = left.m00() * right.m00() + left.m01() * right.m10() + left.m02() * right.m20();
        float g = left.m00() * right.m01() + left.m01() * right.m11() + left.m02() * right.m21();
        float h = left.m00() * right.m02() + left.m01() * right.m12() + left.m02() * right.m22();
        float i = left.m10() * right.m00() + left.m11() * right.m10() + left.m12() * right.m20();
        float j = left.m10() * right.m01() + left.m11() * right.m11() + left.m12() * right.m21();
        float k = left.m10() * right.m02() + left.m11() * right.m12() + left.m12() * right.m22();
        float l = left.m20() * right.m00() + left.m21() * right.m10() + left.m22() * right.m20();
        float m = left.m20() * right.m01() + left.m21() * right.m11() + left.m22() * right.m21();
        float n = left.m20() * right.m02() + left.m21() * right.m12() + left.m22() * right.m22();
        left.m00(f);
        left.m01(g);
        left.m02(h);
        left.m10(i);
        left.m11(j);
        left.m12(k);
        left.m20(l);
        left.m21(m);
        left.m22(n);
    }

    public static void matrix4fMultiply(Matrix4f left, Matrix4f right) {
        float f = left.m00() * right.m00() + left.m01() * right.m10() + left.m02() * right.m20() + left.m03() * right.m30();
        float g = left.m00() * right.m01() + left.m01() * right.m11() + left.m02() * right.m21() + left.m03() * right.m31();
        float h = left.m00() * right.m02() + left.m01() * right.m12() + left.m02() * right.m22() + left.m03() * right.m32();
        float i = left.m00() * right.m03() + left.m01() * right.m13() + left.m02() * right.m23() + left.m03() * right.m33();
        float j = left.m10() * right.m00() + left.m11() * right.m10() + left.m12() * right.m20() + left.m13() * right.m30();
        float k = left.m10() * right.m01() + left.m11() * right.m11() + left.m12() * right.m21() + left.m13() * right.m31();
        float l = left.m10() * right.m02() + left.m11() * right.m12() + left.m12() * right.m22() + left.m13() * right.m32();
        float m = left.m10() * right.m03() + left.m11() * right.m13() + left.m12() * right.m23() + left.m13() * right.m33();
        float n = left.m20() * right.m00() + left.m21() * right.m10() + left.m22() * right.m20() + left.m23() * right.m30();
        float o = left.m20() * right.m01() + left.m21() * right.m11() + left.m22() * right.m21() + left.m23() * right.m31();
        float p = left.m20() * right.m02() + left.m21() * right.m12() + left.m22() * right.m22() + left.m23() * right.m32();
        float q = left.m20() * right.m03() + left.m21() * right.m13() + left.m22() * right.m23() + left.m23() * right.m33();
        float r = left.m30() * right.m00() + left.m31() * right.m10() + left.m32() * right.m20() + left.m33() * right.m30();
        float s = left.m30() * right.m01() + left.m31() * right.m11() + left.m32() * right.m21() + left.m33() * right.m31();
        float t = left.m30() * right.m02() + left.m31() * right.m12() + left.m32() * right.m22() + left.m33() * right.m32();
        float u = left.m30() * right.m03() + left.m31() * right.m13() + left.m32() * right.m23() + left.m33() * right.m33();
        left.m00(f);
        left.m01(g);
        left.m02(h);
        left.m03(i);
        left.m10(j);
        left.m11(k);
        left.m12(l);
        left.m13(m);
        left.m20(n);
        left.m21(o);
        left.m22(p);
        left.m23(q);
        left.m30(r);
        left.m31(s);
        left.m32(t);
        left.m33(u);
    }

    public static Matrix4f matrix4fScale(float x, float y, float z) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00(x);
        matrix4f.m11(y);
        matrix4f.m22(z);
        matrix4f.m33(1.0F);
        return matrix4f;
    }

    public static Matrix4f matrix4fTranslate(float x, float y, float z) {
        Matrix4f matrix4f = new Matrix4f();
        matrix4f.m00(1.0F);
        matrix4f.m11(1.0F);
        matrix4f.m22(1.0F);
        matrix4f.m33(1.0F);
        matrix4f.m03(x);
        matrix4f.m13(y);
        matrix4f.m23(z);
        return matrix4f;
    }

    public static void quaternionHamiltonProduct(Quaternionf left, Quaternionf right) {
        float f = left.x;
        float g = left.y;
        float h = left.z;
        float i = left.w;
        float j = right.x;
        float k = right.y;
        float l = right.z;
        float m = right.w;

        left.x = i * j + f * m + g * l - h * k;
        left.y = i * k - f * l + g * m + h * j;
        left.z = i * l + f * k - g * j + h * m;
        left.w = i * m - f * j - g * k - h * l;
    }

    public static void vec3fTransformation(Vector3f vec, Matrix3f matrix) {
        float f = vec.x;
        float g = vec.y;
        float h = vec.z;
        vec.x = matrix.m00() * f + matrix.m01() * g + matrix.m02() * h;
        vec.y = matrix.m10() * f + matrix.m11() * g + matrix.m12() * h;
        vec.z = matrix.m20() * f + matrix.m21() * g + matrix.m22() * h;
    }

    public static void vec4fTransformation(Vector4f vec, Matrix4f matrix) {
        float f = vec.x;
        float g = vec.y;
        float h = vec.z;
        float i = vec.w;
        vec.x = matrix.m00() * f + matrix.m01() * g + matrix.m02() * h + matrix.m03() * i;
        vec.y = matrix.m10() * f + matrix.m11() * g + matrix.m12() * h + matrix.m13() * i;
        vec.z = matrix.m20() * f + matrix.m21() * g + matrix.m22() * h + matrix.m23() * i;
        vec.w = matrix.m30() * f + matrix.m31() * g + matrix.m32() * h + matrix.m33() * i;
    }
}
