package org.modogthedev.superposition.system.behavior.behaviors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import org.modogthedev.superposition.blockentity.AnalyserBlockEntity;
import org.modogthedev.superposition.system.behavior.Behavior;
import org.modogthedev.superposition.system.behavior.types.ManipulateBehavior;
import org.modogthedev.superposition.system.behavior.types.ScanBehavior;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.DataHelper;

public class SignBehavior extends Behavior implements ScanBehavior, ManipulateBehavior {
    public SignBehavior(ResourceLocation selfReference) {
        super(selfReference);
    }

    @Override
    public void scan(CompoundTag tag, AnalyserBlockEntity analyserBlockEntity, Level level, BlockPos pos, BlockState state) {
        String text = "";
            BlockEntity blockEntity1 = level.getBlockEntity(pos);
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
        if (!text.isEmpty())
            tag.putString(getSelfReference().getPath(),text);
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
}
