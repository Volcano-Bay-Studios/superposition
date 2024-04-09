package org.modogthedev.superposition.networking.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
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

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            Level world = player.level();
            if (world == null || !world.isLoaded(pos))
                return;
            if (!pos.closerThan(player.blockPosition(), maxRange))
                return;
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SyncedBlockEntity) {
                applySettings(player, (SyncedBlockEntity) blockEntity);
                blockEntity.setChanged();
            }
        });
        return true;
    }
    public void applySettings(ServerPlayer player, SyncedBlockEntity blockEntity) {
        blockEntity.writeData(tag);
        blockEntity.sendData();
    }
}
