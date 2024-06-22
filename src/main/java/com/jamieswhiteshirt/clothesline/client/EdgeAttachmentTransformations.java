package com.jamieswhiteshirt.clothesline.client;

import com.jamieswhiteshirt.clothesline.api.AttachmentUnit;
import com.jamieswhiteshirt.clothesline.api.NetworkEdge;
import com.jamieswhiteshirt.clothesline.api.Path;
import com.jamieswhiteshirt.clothesline.common.util.JomlUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class EdgeAttachmentTransformations {
    private static final float EDGE_X = -2.0F / 16.0F;
    private static final float EDGE_Y = 0.0F;

    private final int fromOffset;
    private final int toOffset;
    private final LineProjection projection;
    private final float angleY;
    private final float fromAngleDiff;
    private final float toAngleDiff;

    private EdgeAttachmentTransformations(int fromOffset, int toOffset, LineProjection projection, float angleY, float fromAngleDiff, float toAngleDiff) {
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.projection = projection;
        this.angleY = angleY;
        this.fromAngleDiff = fromAngleDiff;
        this.toAngleDiff = toAngleDiff;
    }

    private static float floorModAngle(float angle) {
        if (angle >= 0.0F) {
            return angle % 360.0F;
        } else {
            return 360.0F + (angle % 360.0F);
        }
    }

    private static float calculateGlobalAngleY(BlockPos delta) {
        return floorModAngle((float)Math.toDegrees(Math.atan2(delta.getZ(), delta.getX())));
    }


    private static float angleBetween(Path.Edge a, Path.Edge b) {
        float angleA = calculateGlobalAngleY(a.getDelta());
        float angleB = calculateGlobalAngleY(b.getDelta());
        return floorModAngle(angleA - angleB);
    }

    public static EdgeAttachmentTransformations build(Path.Edge fromPathEdge, Path.Edge pathEdge, Path.Edge toPathEdge, LineProjection projection) {
        return new EdgeAttachmentTransformations(
            pathEdge.getFromOffset(),
            pathEdge.getToOffset(),
            projection,
            calculateGlobalAngleY(pathEdge.getDelta()),
            angleBetween(fromPathEdge, pathEdge),
            angleBetween(pathEdge, toPathEdge)
        );
    }

    public static EdgeAttachmentTransformations build(NetworkEdge edge, LineProjection p) {
        int index = edge.getIndex();
        List<Path.Edge> edges = edge.getNetwork().getState().getPath().getEdges();
        Path.Edge fromPathEdge = edges.get(Math.floorMod(index - 1, edges.size()));
        Path.Edge toPathEdge = edges.get(Math.floorMod(index + 1, edges.size()));
        return EdgeAttachmentTransformations.build(fromPathEdge, edge.getPathEdge(), toPathEdge, p);
    }

    private float calculateSwingAngle(float momentum, float offset) {
        if (momentum == 0.0D) {
            return 0.0F;
        }
        float t;
        float angleDiff;
        if (momentum > 0.0D) {
            t = offset - fromOffset;
            angleDiff = fromAngleDiff;
        } else {
            t = toOffset - offset;
            angleDiff = toAngleDiff;
        }
        float speedRatio = momentum / AttachmentUnit.UNITS_PER_BLOCK;
        float swingMax = 6.0F * angleDiff * speedRatio * speedRatio;

        return swingMax *
            (float)(Math.exp(-t / (AttachmentUnit.UNITS_PER_BLOCK * 2.0F))) *
            MathHelper.sin((float)(Math.PI * t / AttachmentUnit.UNITS_PER_BLOCK));
    }

    public Matrix4f getL2WForAttachment(float momentum, float offset, float tickDelta) {
        float relativeOffset = offset - fromOffset;
        Vec3d pos = projection.projectRUF(EDGE_X, EDGE_Y, relativeOffset / AttachmentUnit.UNITS_PER_BLOCK);
        float swingAngle = calculateSwingAngle(momentum, offset);

        Quaternionf rotation = JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_Y, -angleY);
        JomlUtil.quaternionHamiltonProduct(rotation, JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_X, swingAngle));

        Matrix4f model = JomlUtil.matrix4fTranslate((float) pos.x, (float) pos.y, (float) pos.z);
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fScale(0.5F, 0.5F, 0.5F));
        JomlUtil.matrix4fMultiply(model, JomlUtil.quaternionToMatrix4f(rotation));
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fTranslate(0.0F, -0.5F, 0.0F));
        return model;
    }

    public Matrix4f getW2LForAttachment(float momentum, float offset, float tickDelta) {
        float relativeOffset = offset - fromOffset;
        Vec3d pos = projection.projectRUF(EDGE_X, EDGE_Y, relativeOffset / AttachmentUnit.UNITS_PER_BLOCK);
        float swingAngle = calculateSwingAngle(momentum, offset);

        Quaternionf rotation = JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_X, -swingAngle);
        JomlUtil.quaternionHamiltonProduct(rotation, JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_Y, angleY));

        Matrix4f model = JomlUtil.matrix4fTranslate(0.0F, 0.5F, 0.0F);
        JomlUtil.matrix4fMultiply(model, JomlUtil.quaternionToMatrix4f(rotation));
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fScale(2.0F, 2.0F, 2.0F));
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fTranslate((float) -pos.x, (float) -pos.y, (float) -pos.z));
        return model;
    }

    public Transformation getL2WForAttachment(float momentum, float offset) {
        float relativeOffset = offset - fromOffset;
        Vec3d pos = projection.projectRUF(EDGE_X, EDGE_Y, relativeOffset / AttachmentUnit.UNITS_PER_BLOCK);
        float swingAngle = calculateSwingAngle(momentum, offset);

        Quaternionf rotation = JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_Y, -angleY);
        JomlUtil.quaternionHamiltonProduct(rotation, JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_X, swingAngle));

        Matrix4f model = JomlUtil.matrix4fTranslate((float) pos.x, (float) pos.y, (float) pos.z);
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fScale(0.5F, 0.5F, 0.5F));
        JomlUtil.matrix4fMultiply(model, JomlUtil.quaternionToMatrix4f(rotation));
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fTranslate(0.0F, -0.5F, 0.0F));
        Matrix3f normal = JomlUtil.quaternionToMatrix3f(rotation);
        return new Transformation(model, normal);
    }

    public Transformation getW2LForAttachment(float momentum, float offset) {
        float relativeOffset = offset - fromOffset;
        Vec3d pos = projection.projectRUF(EDGE_X, EDGE_Y, relativeOffset / AttachmentUnit.UNITS_PER_BLOCK);
        float swingAngle = calculateSwingAngle(momentum, offset);

        Quaternionf rotation = JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_X, -swingAngle);
        JomlUtil.quaternionHamiltonProduct(rotation, JomlUtil.getDegreesQuaternion(JomlUtil.POSITIVE_Y, angleY));

        Matrix4f model = JomlUtil.matrix4fTranslate(0.0F, 0.5F, 0.0F);
        JomlUtil.matrix4fMultiply(model, JomlUtil.quaternionToMatrix4f(rotation));
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fScale(2.0F, 2.0F, 2.0F));
        JomlUtil.matrix4fMultiply(model, JomlUtil.matrix4fTranslate((float) -pos.x, (float) -pos.y, (float) -pos.z));
        Matrix3f normal = JomlUtil.quaternionToMatrix3f(rotation);
        return new Transformation(model, normal);
    }
}
