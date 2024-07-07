package com.jamieswhiteshirt.clothesline.client.raycast;

import com.jamieswhiteshirt.clothesline.api.*;
import com.jamieswhiteshirt.clothesline.api.util.MutableSortedIntMap;
import com.jamieswhiteshirt.clothesline.client.EdgeAttachmentTransformations;
import com.jamieswhiteshirt.clothesline.client.LineProjection;
import com.jamieswhiteshirt.clothesline.client.Transformation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class Raycasting {
    private static final float EDGE_X = -2.0F / 16.0F;
    private static final float EDGE_Y = 0.0F;
    private static final net.minecraft.util.math.Box ATTACHMENT_BOX = new net.minecraft.util.math.Box(-0.5D, -0.5D, -0.5D, 0.5D, 0.5D, 0.5D);

    @Nullable
    public static NetworkRaycastHit raycastNetworks(NetworkManager manager, Ray ray, double maxDistanceSq, float tickDelta) {
        com.jamieswhiteshirt.rtree3i.Box box = com.jamieswhiteshirt.rtree3i.Box.create(
            (int) Math.floor(Math.min(ray.from.x, ray.to.x) - 0.5D),
            (int) Math.floor(Math.min(ray.from.y, ray.to.y) - 0.5D),
            (int) Math.floor(Math.min(ray.from.z, ray.to.z) - 0.5D),
            (int) Math.ceil(Math.max(ray.from.x, ray.to.x) + 0.5D),
            (int) Math.ceil(Math.max(ray.from.y, ray.to.y) + 0.5D),
            (int) Math.ceil(Math.max(ray.from.z, ray.to.z) + 0.5D)
        );

        NetworkRaycastHit hit = null;
        List<NetworkEdge> edges = manager.getNetworks().getEdges().values(box::intersectsClosed).collect(Collectors.toList());
        for (NetworkEdge edge : edges) {
            NetworkRaycastHit hitCandidate = raycastEdge(ray, edge, maxDistanceSq, tickDelta);
            if (hitCandidate != null && hitCandidate.distanceSq < maxDistanceSq) {
                maxDistanceSq = hitCandidate.distanceSq;
                hit = hitCandidate;
            }
        }

        return hit;
    }

    @Nullable
    private static NetworkRaycastHit raycastEdge(Ray viewRay, NetworkEdge edge, double maxDistanceSq, float tickDelta) {
        Path.Edge pathEdge = edge.getPathEdge();
        LineProjection projection = LineProjection.create(edge);
        NetworkRaycastHit hit = null;

        Vec3d from = projection.projectRUF(EDGE_X, EDGE_Y, 0.0F);
        Vec3d to = projection.projectRUF(EDGE_X, EDGE_Y, (float) edge.getPathEdge().getLength() / AttachmentUnit.UNITS_PER_BLOCK);
        Ray edgeRay = new Ray(from, to);

        double b = viewRay.delta.dotProduct(edgeRay.delta);
        Vec3d w0 = viewRay.from.subtract(edgeRay.from);
        double denominator = viewRay.lengthSq * edgeRay.lengthSq - b * b;
        if (denominator != 0.0D) {
            double d = viewRay.delta.dotProduct(w0);
            double e = edgeRay.delta.dotProduct(w0);
            double viewDeltaScalar = MathHelper.clamp((b * e - edgeRay.lengthSq * d) / denominator, 0.0D, 1.0D);
            double edgeDeltaScalar = MathHelper.clamp((viewRay.lengthSq * e - b * d) / denominator, 0.0D, 1.0D);

            Vec3d viewNear = viewRay.project(viewDeltaScalar);
            Vec3d edgeNear = edgeRay.project(edgeDeltaScalar);

            Vec3d nearDelta = edgeNear.subtract(viewNear);
            if (nearDelta.lengthSquared() < (1.0D / 16.0D) * (1.0D / 16.0D)) {
                double rayLengthSquared = (viewNear.subtract(viewRay.from)).lengthSquared();
                if (rayLengthSquared < maxDistanceSq) {
                    double offset = pathEdge.getFromOffset() * (1.0D - edgeDeltaScalar) + pathEdge.getToOffset() * edgeDeltaScalar;
                    hit = new EdgeRaycastHit(rayLengthSquared, edge, offset);
                }
            }
        }

        NetworkState state = edge.getNetwork().getState();
        float fromAttachmentKey = state.offsetToAttachmentKey(pathEdge.getFromOffset(), tickDelta);
        float toAttachmentKey = state.offsetToAttachmentKey(pathEdge.getToOffset(), tickDelta);
        List<MutableSortedIntMap.Entry<ItemStack>> attachments = state.getAttachmentsInRange((int) fromAttachmentKey, (int) toAttachmentKey);
        if (!attachments.isEmpty()) {
            Vector4f lFrom = new Vector4f();
            Vector4f lTo = new Vector4f();
            Vector4f wHit = new Vector4f();

            EdgeAttachmentTransformations transformations = EdgeAttachmentTransformations.build(edge, projection);
            for (MutableSortedIntMap.Entry<ItemStack> attachment : attachments) {
                float attachmentOffset = state.attachmentKeyToOffset(attachment.getKey(), tickDelta);
                float momentum = state.getMomentum(tickDelta);
                // Local space to world space matrix
                Matrix4f l2w = transformations.getL2WForAttachment(momentum, attachmentOffset, tickDelta);
                // World space to local space matrix
                Matrix4f w2l = transformations.getW2LForAttachment(momentum, attachmentOffset, tickDelta);

                lFrom.set((float) viewRay.from.x, (float) viewRay.from.y, (float) viewRay.from.z, 1.0F);
                lFrom.mul(w2l);
                lTo.set((float) viewRay.to.x, (float) viewRay.to.y, (float) viewRay.to.z, 1.0F);
                lTo.mul(w2l);

                Optional<Vec3d> lResult = ATTACHMENT_BOX.raycast(new Vec3d(lFrom.x, lFrom.y, lFrom.z), new Vec3d(lTo.x, lTo.y, lTo.z));
                if (lResult.isPresent()) {
                    Vec3d lHit = lResult.get();
                    wHit.set((float) lHit.x, (float) lHit.y, (float) lHit.z, 1.0F);
                    wHit.mul(l2w);
                    double distanceSq = new Vec3d(wHit.x, wHit.y, wHit.z).squaredDistanceTo(viewRay.from);
                    if (distanceSq < maxDistanceSq) {
                        maxDistanceSq = distanceSq;
                        hit = new AttachmentRaycastHit(distanceSq, edge, attachment.getKey(), new Transformation(l2w, new Matrix3f(l2w)));
                    }
                }
            }
        }

        return hit;
    }
}
