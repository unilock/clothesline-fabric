package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.api.Line;
import com.jamieswhiteshirt.clothesline.api.NetworkEdge;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public final class LineProjection {
    private final Vec3d origin;
    private final Vec3d right;
    private final Vec3d up;
    private final Vec3d forward;

    public LineProjection(Vec3d origin, Vec3d right, Vec3d up, Vec3d forward) {
        this.origin = origin;
        this.right = right;
        this.up = up;
        this.forward = forward;
    }

    public Vec3d projectRUF(float r, float u, float f) {
        return origin.add(right.multiply(r)).add(up.multiply(u)).add(forward.multiply(f));
    }

    public Transformation getTransformation() {
        Matrix4f model = new Matrix4f(
            (float) right.x, (float) right.y, (float) right.z, 0.0F,
            (float) up.x, (float) up.y, (float) up.z, 0.0F,
            (float) forward.x, (float) forward.y, (float) forward.z, 0.0F,
            (float) origin.x, (float) origin.y, (float) origin.z, 1.0F
        );
        Matrix3f normal = new Matrix3f(
            (float) right.x, (float) right.y, (float) right.z,
            (float) up.x, (float) up.y, (float) up.z,
            (float) forward.x, (float) forward.y, (float) forward.z
        );

        return new Transformation(model, normal);
    }

    public static LineProjection create(Vec3d from, Vec3d to) {
        // The normal vector facing from the from pos to the to pos
        Vec3d forward = to.subtract(from).normalize();
        // The normal vector facing right to the forward normal (on the y plane)
        Vec3d rightNormal = forward.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (rightNormal.equals(Vec3d.ZERO)) {
            // We are looking straight up or down so the right normal is undefined
            // Let it be x if we are looking straight up or -x if we are looking straight down
            rightNormal = new Vec3d(Math.signum(forward.y), 0.0D, 0.0D);
        }
        // The normal vector facing up from the forward normal (on the right normal plane)
        Vec3d upNormal = rightNormal.crossProduct(forward);

        return new LineProjection(from, rightNormal, upNormal, forward);
    }

    public static LineProjection create(Line line) {
        return create(line.getFromVec(), line.getToVec());
    }

    public static LineProjection create(NetworkEdge edge) {
        return create(edge.getPathEdge().getLine());
    }

    public static Transformation createTransformation(Vec3d from, Vec3d to) {
        // The normal vector facing from the from pos to the to pos
        Vec3d forward = to.subtract(from).normalize();
        // The normal vector facing right to the forward normal (on the y plane)
        Vec3d rightNormal = forward.crossProduct(new Vec3d(0.0D, 1.0D, 0.0D)).normalize();
        if (rightNormal.equals(Vec3d.ZERO)) {
            // We are looking straight up or down so the right normal is undefined
            // Let it be x if we are looking straight up or -x if we are looking straight down
            rightNormal = new Vec3d(Math.signum(forward.y), 0.0D, 0.0D);
        }
        // The normal vector facing up from the forward normal (on the right normal plane)
        Vec3d upNormal = rightNormal.crossProduct(forward);

        Matrix4f model = new Matrix4f(
            (float) rightNormal.x, (float) rightNormal.y, (float) rightNormal.z, 0.0F,
            (float) upNormal.x, (float) upNormal.y, (float) upNormal.z, 0.0F,
            (float) forward.x, (float) forward.y, (float) forward.z, 0.0F,
            (float) from.x, (float) from.y, (float) from.z, 1.0F
        );
        Matrix3f normal = new Matrix3f(
            (float) rightNormal.x, (float) rightNormal.y, (float) rightNormal.z,
            (float) upNormal.x, (float) upNormal.y, (float) upNormal.z,
            (float) forward.x, (float) forward.y, (float) forward.z
        );

        return new Transformation(model, normal);
    }

    public static Transformation createTransformation(Line line) {
        return createTransformation(line.getFromVec(), line.getToVec());
    }

    public static Transformation createTransformation(NetworkEdge edge) {
        return createTransformation(edge.getPathEdge().getLine());
    }
}
