package org.modogthedev.superposition.core;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import org.modogthedev.superposition.Superposition;

public class SuperpositionPartials {

    public static final PartialModel PANEL_SURFACE = block("panel/surface");
    public static final PartialModel PANEL_SURFACE_LEFT = block("panel/surface_left");
    public static final PartialModel PANEL_SURFACE_MIDDLE = block("panel/surface_middle");
    public static final PartialModel PANEL_SURFACE_RIGHT = block("panel/surface_right");
    public static final PartialModel PANEL_FRONT_LEGS = block("panel/front_legs");
    public static final PartialModel PANEL_BACK_LEGS = block("panel/back_legs");

    public static PartialModel widget(String path) {
        return PartialModel.of(Superposition.id("block/widget/" + path));
    }

    public static PartialModel block(String path) {
        return PartialModel.of(Superposition.id("block/" + path));
    }

    public static void bootstrap(){

    }
}
