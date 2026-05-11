package org.modogthedev.superposition.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.modogthedev.superposition.util.DelegateVoxelShape;
import org.modogthedev.superposition.util.DynamicShapedBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {


    @Inject(method = "clipWithInteractionOverride", at = @At("HEAD"), cancellable = true)
    default void clip(Vec3 startVec, Vec3 endVec, BlockPos pos, VoxelShape shape, BlockState state, CallbackInfoReturnable<BlockHitResult> cir) {
        DelegateVoxelShape delegate = (DelegateVoxelShape) shape;
        DynamicShapedBlockEntity dynamicShape = delegate.getDynamicShape();
        if (dynamicShape != null) {
            for (DynamicShapedBlockEntity.DynamicShape dynamicShapeShape : dynamicShape.getShapes()) {
                Matrix4f mat = dynamicShapeShape.transformation();
                Matrix4f invMat = new Matrix4f(mat).invert();

                Vector3f start = new Vector3f((float)(startVec.x - pos.getX()), (float)(startVec.y - pos.getY()), (float)(startVec.z - pos.getZ()));
                Vector3f end = new Vector3f((float)(endVec.x - pos.getX()), (float)(endVec.y - pos.getY()), (float)(endVec.z - pos.getZ()));

                start.mulPosition(invMat);
                end.mulPosition(invMat);

                startVec = new Vec3(start);
                endVec = new Vec3(end);

                BlockHitResult blockhitresult = dynamicShapeShape.shape().clip(startVec, endVec, BlockPos.ZERO);
                if (blockhitresult != null) {
                    Vec3 hit = blockhitresult.getLocation();

                    hit = new Vec3(new Vector3f((float) hit.x, (float) hit.y, (float) hit.z).mulPosition(mat));
                    hit = new Vec3(hit.x + pos.getX(), hit.y + pos.getY(), hit.z + pos.getZ());

                    BlockHitResult returnValue = new BlockHitResult(hit, blockhitresult.getDirection(), pos, blockhitresult.isInside());
                    cir.setReturnValue(returnValue);
                }
            }
        }
    }
}
