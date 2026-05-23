package org.modogthedev.superposition.system.card;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.ArrayList;
import java.util.List;

public abstract class ArraySetExecutableAction extends Action {
    public ArraySetExecutableAction(ResourceLocation action, Information info) {
        super(action, info);
    }

    @Override
    public void execute(NodePorts input, NodePorts output, Level level, BlockPos pos) {
        List<String> keys = arrayKeys();
        List<List<Signal>> lists = new ArrayList<>();

        int smallest = Integer.MAX_VALUE;
        for (String key : keys) {
            List<Signal> newList = input.getSignals(key);
            lists.add(newList);
            smallest = Math.min(newList.size(),smallest);
        }

        List<Signal> out = new ArrayList<>();

        for (int i = 0; i < smallest; i++) {
            Signal[] signals = new Signal[lists.size()];
            for (int j = 0; j < lists.size(); j++) {
                signals[j] = lists.get(j).get(i);
            }

            out.add(executeArray(signals));
        }
        output.putSignals(outString(),out);
    }

    @Override
    protected NodePorts.Builder buildInputPorts(NodePorts.Builder builder) {
        for (String key : arrayKeys()) {
            builder.addPort(key);
        }
        return builder;
    }

    protected abstract List<String> arrayKeys();

    /**
     * This will be executed with one signal in each element of the array corresponding to each key, in the order they are placed in the list.
     */
    protected abstract Signal executeArray(Signal[] signals);
}
