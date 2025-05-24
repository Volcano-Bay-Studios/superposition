package org.modogthedev.superposition.system.cards;

import org.joml.Vector2f;
import org.modogthedev.superposition.screens.utils.Bounds;

public class Attachment {
    private Vector2f position = new Vector2f();
    private Node node;
    private Attachment target = null;
    private int snapMode = 0;

    public Attachment(Vector2f position, Node node) {
        this.position.set(position);
        this.node = node;
    }

    public Vector2f getPosition() {
        return position;
    }

    public boolean isColliding(float x, float y) {
        return Bounds.isColliding(new Vector2f(8f), getAbsolutePosition().x, getAbsolutePosition().y, x, y);
    }

    public Vector2f getAbsolutePosition() {
        return new Vector2f(position.x + node.getPosition().x, position.y + node.getPosition().y);
    }

    public Attachment getTarget() {
        return target;
    }

    public int getSnapMode() {
        return snapMode;
    }

    public void incrementSnapMode(int amount) {
        this.snapMode = (this.snapMode + amount) % 4;
    }

    /**
     * Sets the attachment to a segment
     * @param position position will be modified to be relative to the node
     */
    public void setSegment(Vector2f position) {
        this.target = new SegmentAttachment(position.sub(node.getPosition()), this);
    }

    public void clearTarget() {
        this.target = null;
    }

    public static class SegmentAttachment extends Attachment {
        public SegmentAttachment(Vector2f position, Attachment parent) {
            super(position, parent.node);
        }
    }
}
