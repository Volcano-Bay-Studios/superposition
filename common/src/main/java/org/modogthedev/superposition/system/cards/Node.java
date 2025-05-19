package org.modogthedev.superposition.system.cards;

import org.joml.Vector2f;
import org.modogthedev.superposition.screens.utils.Bounds;
import org.modogthedev.superposition.system.signal.Signal;

import java.util.UUID;

public class Node {
    private Action action = null;
    private Node target = null;
    private UUID targetUUID = null;
    private int inputIndex = 0;
    private final Card card;
    private Vector2f position = new Vector2f();
    private Vector2f size = new Vector2f(10,10);
    private Signal[] signals;

    public Node(Card card) {
        this.card = card;
        updateSize(0);
    }

    private void evaluateLink() {
        target = card.getNodes().get(targetUUID);
    }

    public void setTargetUUID(UUID targetUUID) {
        this.targetUUID = targetUUID;
        evaluateLink();
    }

    public Node getTarget() {
        return target;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getSize() {
        return size;
    }

    public void updateAction(Action action) {
        this.action = action;
        if (action instanceof ExecutableAction executableAction) {
            updateSize(executableAction.getParameterCount());
        }
    }

    public void updateSize(int size) {
        signals = new Signal[size];
        calculateSize();
    }

    private void calculateSize() {
        size.x = 20;
        size.y = 20;
        if (signals != null) {
            size.y = 10 + (10 * Math.max(1,signals.length));
        }
    }

    public boolean isColliding(int x, int y) {
        return Bounds.isColliding(this, x, y);
    }
}
