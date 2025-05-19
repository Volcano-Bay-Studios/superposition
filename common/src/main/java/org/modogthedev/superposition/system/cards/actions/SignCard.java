package org.modogthedev.superposition.system.cards.actions;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.cards.*;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.DataHelper;

public class SignCard extends Action implements ScanAction, OutAction, ManipulateAction {
    public SignCard(ResourceLocation card) {
        super(card);
    }


    @Override
    public void scan(Signal signal, BlockEntity blockEntity) {
        String text = "";
        if (blockEntity instanceof AnalyserBlockEntity analyserBlockEntity) {
            BlockEntity blockEntity1 = blockEntity.getLevel().getBlockEntity(analyserBlockEntity.getAnalysisPosition());
            if (blockEntity1 instanceof SignBlockEntity signBlockEntity) {
                for (Component component : signBlockEntity.getFrontText().getMessages(true)) {
                    if (!component.getString().isEmpty())
                        text = text.concat((text.isEmpty() ? "" : " ") + component.getString());
                }
                for (Component component : signBlockEntity.getBackText().getMessages(true)) {
                    if (!component.getString().isEmpty())
                        text = text.concat((text.isEmpty() ? "" : " ") + component.getString());
                }
            }
        }
        if (!text.isEmpty())
            signal.encode(text);
    }

    @Override
    public void manipulate(Signal signal, Level level, BlockPos pos) {
        if (signal != null && signal.getEncodedData() != null) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SignBlockEntity signBlockEntity) {
                String string = DataHelper.getStringKey(signal, "line");
                if (string != null) {
                    signBlockEntity.setText(new SignText().setMessage(0, Component.literal(string)), true);
                }
            }
        }
    }

    @Override
    public void addOutbound(CompoundTag tag, Signal signal) {
        String string = DataHelper.getStringValue(signal);
        if (string != null) {
            tag.putString("line", string);
        }
    }
}
