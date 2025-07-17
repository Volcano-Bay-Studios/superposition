package org.modogthedev.superposition.networking.handler;

import foundry.veil.api.network.VeilPacketManager;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.item.FilterItem;
import org.modogthedev.superposition.networking.packet.*;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;
import org.modogthedev.superposition.util.SyncedBlockEntity;
import oshi.util.tuples.Pair;

import java.util.List;

public class SuperpositionServerPacketHandler {

    public static void handleBlockEntityModification(BlockEntityModificationC2SPacket packet, ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = player.serverLevel();

        BlockPos pos = packet.pos();
        if (!level.isLoaded(pos)) {
            return;
        }

        double dist = player.position().distanceToSqr(pos.getCenter());
        if (dist > 64) {
            return;
        }

        if (level.getBlockEntity(pos) instanceof SyncedBlockEntity be) {
            be.loadSyncedData(packet.tag());
            be.sendData();
            be.setChanged();
        }
    }

    public static void handleFilterItemModification(FilterItemModificationC2SPacket packet, ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        for (InteractionHand hand : InteractionHand.values()) {
            ItemStack stack = player.getItemInHand(hand);
            if ((stack.getItem() instanceof FilterItem)) {
                stack.set(DataComponents.CUSTOM_DATA, CustomData.of(packet.tag()));
                break;
            }
        }
    }

    public static void handleDropCable(PlayerDropCableC2SPacket packet, ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = player.serverLevel();
        for (Cable cable : CableManager.getLevelCables(level)) {
            if (cable.hasPlayerHolding(player.getId())) {
                cable.stopPlayerDrag(player.getId());
                CableSyncS2CPacket response = new CableSyncS2CPacket(cable);

                List<RopeNode> points = cable.getPoints();
                Vec3 pos = points.getFirst().getPosition();
                VeilPacketManager.around(player, level, pos.x, pos.y, pos.z, cable.getPoints().size() + 100).sendPacket(response);
                break;
            }
        }
    }

    public static void handleGrabCable(PlayerGrabCableC2SPacket packet, ServerPacketContext ctx) {
        ServerPlayer player = ctx.player();
        ServerLevel level = player.serverLevel();

        CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, player.level());
        Pair<Cable, RopeNode> rayCast = cableClipResult.filteredRayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f, packet.id(), !player.isShiftKeyDown());
        if (rayCast == null || !rayCast.getA().getId().equals(packet.id())) {
            Cable cable = CableManager.getCable(level, packet.id());
            ctx.sendPacket(cable != null ? new CableSyncS2CPacket(cable) : new CableSyncS2CPacket(packet.id()));
            return;
        }

        Cable cable = rayCast.getA();
        cable.addPlayerHoldingPoint(player.getId(), cable.getPointIndex(rayCast.getB()));

        List<RopeNode> points = cable.getPoints();
        Vec3 pos = points.getFirst().getPosition();
        VeilPacketManager.around(player, level, pos.x, pos.y, pos.z, cable.getPoints().size() + 100).sendPacket(new CableSyncS2CPacket(cable));
    }
}
