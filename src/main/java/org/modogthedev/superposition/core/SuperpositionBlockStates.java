package org.modogthedev.superposition.core;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class SuperpositionBlockStates {
    public static final IntegerProperty FREQUENCY = IntegerProperty.create("frequency",0,12);
    public static final IntegerProperty AMPLITUDE = IntegerProperty.create("amplitude",0,10);
    public static final BooleanProperty SWAP_SIDES = BooleanProperty.create("swap");
    public static final BooleanProperty ON = BooleanProperty.create("on");
    public static final BooleanProperty SHORT = BooleanProperty.create("short");

}
