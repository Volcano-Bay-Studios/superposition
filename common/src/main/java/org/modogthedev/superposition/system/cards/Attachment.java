package org.modogthedev.superposition.system.cards;

import org.joml.Vector2f;
import org.modogthedev.superposition.screens.utils.Bounds;

public class Attachment {
    private final Vector2f position = new Vector2f();
    private final Node node;
    private Attachment target = null;
    private int snapMode = 2;

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

    public void setSnapMode(int snapMode) {
        this.snapMode = snapMode;
    }

    /**
     * Sets the attachment to a segment
     *
     * @param position position will be modified to be relative to the node
     */
    public void setSegment(Vector2f position) {
        if (target == null) {
            this.target = new SegmentAttachment(position.sub(node.getPosition()), this);
        } else {
            this.target.getPosition().set(position.sub(node.getPosition()));
        }
        if (this.target instanceof SegmentAttachment segmentAttachment) {
            segmentAttachment.setParent(this);
        }
    }

    public void setTarget(Attachment target) {
        this.target = target;
        if (this.target instanceof SegmentAttachment segmentAttachment) {
            segmentAttachment.setParent(this);
        }
    }

    public void clearTarget() {
        this.target = null;
    }

    public static class SegmentAttachment extends Attachment {
        private Attachment parent;

        public SegmentAttachment(Vector2f position, Attachment parent) {
            super(position, parent.node);
            this.parent = parent;
        }

        public Attachment getParent() {
            return parent;
        }

        public void setParent(Attachment parent) {
            this.parent = parent;
        }
    }

    public static class InputAttachment extends Attachment {
        public InputAttachment(Vector2f position, Node node) {
            super(position, node);
        }
    }
}
