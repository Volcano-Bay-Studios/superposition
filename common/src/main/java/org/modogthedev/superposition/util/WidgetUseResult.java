package org.modogthedev.superposition.util;

public enum WidgetUseResult {
    CLICK(true),
    HOLD(true,0),
    PASS(false);

    WidgetUseResult(boolean consumesAction) {
        this.consumesAction = consumesAction;
        consumeTime = 4;
    }
    WidgetUseResult(boolean consumesAction,int consumeTime) {
        this.consumeTime = consumeTime;
        this.consumesAction = consumesAction;
    }

    private final int consumeTime;

    private final boolean consumesAction;

    public int getConsumeTime() {
        return consumeTime;
    }

    public boolean consumesAction() {
        return consumesAction;
    }
}
