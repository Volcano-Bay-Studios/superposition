package org.modogthedev.superposition.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.core.SuperpositionMessages;
import org.modogthedev.superposition.networking.packet.CableSyncS2CPacket;
import org.modogthedev.superposition.networking.packet.PlayerGrabCableC2SPacket;
import org.modogthedev.superposition.system.cable.Cable;
import org.modogthedev.superposition.system.cable.CableClipResult;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.CarabinerManager;
import oshi.util.tuples.Pair;

public class CarabinerItem extends Item {
    public CarabinerItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        CableClipResult cableClipResult = new CableClipResult(player.getEyePosition(), 8, player.level());
        Pair<Cable, Cable.Point> rayCast = cableClipResult.rayCastForClosest(player.getEyePosition().add(player.getEyePosition().add(player.getForward().subtract(player.getEyePosition())).scale(5)), .7f);
        if (rayCast != null) {
            CarabinerManager.attachPlayer(player, rayCast.getB());
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return super.use(level, player, usedHand);
    }
}