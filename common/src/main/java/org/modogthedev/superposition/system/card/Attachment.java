package org.modogthedev.superposition.system.card;

import org.joml.Vector2f;
import org.modogthedev.superposition.screens.utils.Bounds;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Attachment {
    private final Vector2f position = new Vector2f();
    private final Node node;
    private Attachment finalTargetAttachment = null;
    private Attachment target = null;
    private UUID targetUUID = null;
    private int snapMode = 3;

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
        if (target == null && targetUUID != null) {
            Attachment found = node.getCard().findAttachment(targetUUID);
            if (found != null) {
                target = found;
            }
        }
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

    public Attachment getFinalTargetAttachment() {
        if (finalTargetAttachment == null) {
            List<Attachment> attachments = new ArrayList<>();
            exploreAttachment(this, attachments);
            if (attachments.getLast() == this) {
                return null;
            }
            finalTargetAttachment = attachments.getLast();
        }
        return finalTargetAttachment;
    }

    public Node getNode() {
        return node;
    }

    /**
     * This method is recursive and needs a limit
     *
     * @param attachment
     * @return
     */
    private static void exploreAttachment(Attachment attachment, List<Attachment> attachments) {
        if (attachments.contains(attachment)) {
            return;
        }
        attachments.add(attachment);
        if (attachment.getTarget() != null) {
            exploreAttachment(attachment.getTarget(), attachments);
        }
    }

    public UUID getTargetUUID() {
        return targetUUID;
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
        if (this.target instanceof InputAttachment inputAttachment) {
            targetUUID = inputAttachment.getUuid();
        }
        finalTargetAttachment = null;
    }

    public void setTarget(Attachment target) {
        this.target = target;
        if (this.target instanceof SegmentAttachment segmentAttachment) {
            segmentAttachment.setParent(this);
        }
        if (this.target instanceof InputAttachment inputAttachment) {
            targetUUID = inputAttachment.getUuid();
        }
        finalTargetAttachment = null;
    }

    public void setTargetUUID(UUID targetUUID) {
        this.targetUUID = targetUUID;
    }

    public void clearTarget() {
        this.target = null;
        this.targetUUID = null;
        finalTargetAttachment = null;
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
        private int index = 0;
        private UUID uuid;

        public InputAttachment(Vector2f position, Node node, int index, UUID uuid) {
            super(position, node);
            this.index = index;
            this.uuid = uuid;
        }

        public UUID getUuid() {
            return uuid;
        }

        public int getIndex() {
            return index;
        }
    }
}
