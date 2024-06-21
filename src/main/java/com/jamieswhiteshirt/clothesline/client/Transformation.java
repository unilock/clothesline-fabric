package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.common.util.JomlUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public final class Transformation {
    private final Matrix4f model;
    private final Matrix3f normal;

    public Transformation(Matrix4f model, Matrix3f normal) {
        this.model = model;
        this.normal = normal;
    }

    public Matrix4f getModel() {
        return model;
    }

    public Matrix3f getNormal() {
        return normal;
    }

    public void apply(MatrixStack matrices) {
        JomlUtil.matrix4fMultiply(matrices.peek().getPositionMatrix(), model);
        JomlUtil.matrix3fMultiply(matrices.peek().getNormalMatrix(), normal);
    }
}
