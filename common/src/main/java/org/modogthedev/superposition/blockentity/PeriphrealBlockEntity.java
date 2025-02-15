package org.modogthedev.superposition.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.modogthedev.superposition.core.SuperpositionCards;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.cards.Card;
import org.modogthedev.superposition.system.cards.cards.PeriphrealCard;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.system.signal.SignalManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeriphrealBlockEntity extends SignalActorBlockEntity {
    private Card card;
    private final Signal processSignal;

    {
        Vec3 center = getBlockPos().getCenter();
        processSignal = new Signal(new Vector3d(center.x, center.y, center.z), level, SuperpositionConstants.periphrealFrequency, 1, SuperpositionConstants.periphrealFrequency / 100000);
    }

    public PeriphrealBlockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public void tick() {
        Signal signal = SignalManager.randomSignal(putSignals);
        if (signal != null && signal.getEncodedData() != null) {
            CompoundTag tag = signal.getEncodedData().compoundTagData();
            if (tag.contains("id",99)) {
                int id = tag.getInt("id");
                Card card1 = SuperpositionCards.CARDS.asVanillaRegistry().byId(id);
                if (card1 != null)
                    card = card1.copy();
            }
        }
        if (card != null && card instanceof PeriphrealCard periphrealCard)
            periphrealCard.returnSignal(processSignal,this);
        super.tick();
    }

    @Override
    public List<Signal> getSignals() {
        return new ArrayList<>(Collections.singleton(processSignal));
    }
}
