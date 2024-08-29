package org.modogthedev.superposition.networking.packet;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.modogthedev.superposition.util.SyncedBlockEntity;

import java.util.function.Supplier;

public class BlockEntityModificationC2SPacket {
    private final CompoundTag tag;
    private final BlockPos pos;
    public int maxRange = 20;

    public BlockEntityModificationC2SPacket(CompoundTag tag, BlockPos pos) {
        this.tag = tag;
        this.pos = pos;
    }

    public BlockEntityModificationC2SPacket(FriendlyByteBuf buf) {
        this.tag = buf.readNbt();
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkManager.PacketContext> supplier) {
        var context = supplier.get();

        context.queue(() -> {
            var player = context.getPlayer();

            if (!(player instanceof ServerPlayer serverPlayer))
                return;

            Level world = player.level();

            if (!world.isLoaded(pos))
                return;

            var dist = serverPlayer.position().distanceTo(pos.getCenter());

            if (dist > 8)
                return;

            BlockEntity blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof SyncedBlockEntity syncedBlockEntity) {
                applySettings(serverPlayer, syncedBlockEntity);
                blockEntity.setChanged();
            }
        });
    }
    public void applySettings(ServerPlayer player, SyncedBlockEntity blockEntity) {
        blockEntity.loadSyncedData(tag);
        blockEntity.sendData();
    }
}
