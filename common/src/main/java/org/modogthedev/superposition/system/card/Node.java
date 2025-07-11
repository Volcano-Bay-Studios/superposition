package org.modogthedev.superposition.system.card;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import org.joml.Vector2f;
import org.modogthedev.superposition.screens.utils.Bounds;
import org.modogthedev.superposition.system.signal.Signal;
import org.modogthedev.superposition.util.SignalHelper;
import org.modogthedev.superposition.util.SuperpositionMth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Node {
    private Action action = null;
    private Node target = null;
    private final Card card;
    private final Vector2f position = new Vector2f();
    private final Vector2f size = new Vector2f(10, 10);
    private List<List<Signal>> signals;
    private final List<Attachment> attachments = new ArrayList<>();
    private String issue = null;

    public Node(Card card) {
        this.card = card;
        updateSize(0);
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
        action.setNode(this);
        if (action instanceof ExecutableAction executableAction) {
            updateSize(executableAction.getParameterCount());
        }
    }

    public void fillData(int index, List<Signal> signals, Level level, BlockPos pos) {
        List<Signal> fill = this.signals.get(index); // Add signals to node buffer
        SignalHelper.updateSignalList(fill,signals);
        int maxSize = 0;
        for (List<Signal> signalList : this.signals) { // Check if all signals have been submitted
            if (signalList.isEmpty()) {
                return;
            }
            maxSize = Math.max(maxSize, signalList.size());
        }
        List<Signal> executionSignals = new ArrayList<>(); // Fill this list so that each sequential signal is from a different list, this maintains order in multisignal lists. This also means that AnyModifyActions will be executed on single lists if any of the lists are empty
        if (this.signals.size() > 1) {
            for (int i = 0; i < maxSize; i++) {
                for (List<Signal> signalList : this.signals) {
                    if (i < signalList.size()) {
                        executionSignals.add(signalList.get(i));
                    }
                }
            }
        } else {
            executionSignals.addAll(this.signals.getFirst());
        }
        execute(executionSignals, level, pos); // Complete execution
    }

    public void execute(List<Signal> signals, Level level, BlockPos pos) {
        if (action instanceof ExecutableAction executableAction && !signals.isEmpty()) {
            List<Signal> returns = executableAction.execute(signals, level, pos);
            for (int i = 0; i < executableAction.getOutputCount(); i++) {
                Attachment attachment = attachments.get(i);
                Attachment target = attachment.getFinalTargetAttachment();
                if (target != null && attachment.getNode() != target.getNode() && target instanceof Attachment.InputAttachment inputAttachment) {
                    target.getNode().fillData(inputAttachment.getIndex(), executableAction.sameOutput() ? new ArrayList<>(returns) : List.of(returns.get(i)), level, pos);
                }
            }
        }
    }

    public Card getCard() {
        return card;
    }

    public Action getAction() {
        return action;
    }

    public void updateSize(int size) {
        signals = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            signals.add(new ArrayList<>());
        }
        calculateSize();
    }


    public void clearForExecution() {
//        for (List<Signal> signalList : signals) {
//            signalList.clear();
//        }
    }

    private void calculateSize() {
        size.x = 20;
        size.y = 20;
        if (signals != null) {
            size.y = 12 + (8 * Math.max(1, getInputCount() - 1));
        }

        attachments.clear();
        if (action instanceof ExecutableAction executableAction) {
            int count = executableAction.getOutputCount();
            for (int i = 0; i < count; i++) {
                attachments.add(new Attachment(new Vector2f(size.x / 2, i * 8 - (Math.max(0, executableAction.getOutputCount() - 1) * 4)) , this));
            }

        }

        int inputCount = getInputCount();
        for (int i = 0; i < inputCount; i++) {
            attachments.add(new Attachment.InputAttachment(new Vector2f(-size.x / 2, i * 8 - (Math.max(0, getInputCount() - 1) * 4)), this, i, UUID.randomUUID()));
        }
    }

    public int getInputCount() {
        return signals.size();
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public boolean isColliding(float x, float y) {
        return Bounds.isColliding(this, x, y);
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putFloat("x", position.x());
        tag.putFloat("y", position.y());
        if (action != null) {
            tag.put("action", action.save(new CompoundTag()));
        }
        ListTag attachments = new ListTag();
        for (Attachment attachment : this.attachments) { // Start attachment collection
            CompoundTag attachmentTag = new CompoundTag();
            List<Attachment> collected = new ArrayList<>();
            exploreAttachment(attachment, collected);

            attachmentTag.putFloat("x", attachment.getPosition().x()); // Put high level attachment data
            attachmentTag.putFloat("y", attachment.getPosition().y());

            if (attachment instanceof Attachment.InputAttachment inputAttachment) { // Encode input data
                attachmentTag.putUUID("uuid",inputAttachment.getUuid());
                attachmentTag.putInt("index",inputAttachment.getIndex());
            }

            ListTag internalAttachments = new ListTag();
            for (Attachment segment : collected) { // Encode segment data
                if (segment instanceof Attachment.SegmentAttachment segmentAttachment) {
                    CompoundTag segmentTag = new CompoundTag();
                    segmentTag.putFloat("x", segment.getPosition().x());
                    segmentTag.putFloat("y", segment.getPosition().y());
                    if (segmentAttachment.getTargetUUID() != null) {
                        segmentTag.putUUID("targetUUID", segmentAttachment.getTargetUUID());
                    }
                    internalAttachments.add(segmentTag);
                }
            }
            if (!internalAttachments.isEmpty()) { // Store segment data if present
                attachmentTag.put("segments",internalAttachments);
            } else if (attachment.getTargetUUID() != null) {
                attachmentTag.putUUID("targetUUID", attachment.getTargetUUID());
            }

            attachments.add(attachmentTag);
        }
        tag.put("attachments", attachments);
        // save attachments
        return tag;
    }

    public CompoundTag load(CompoundTag tag) {
        position.set(tag.getFloat("x"), tag.getFloat("y"));
        if (tag.contains("action")) {
            Action newAction = Action.loadNew(tag.getCompound("action"));
            if (newAction != null) {
                updateAction(newAction.copy());
            }
        }
        if (tag.contains("attachments")) {
            this.attachments.clear();
            ListTag attachments = tag.getList("attachments", 10);
            for (int i = 0; i < attachments.size(); i++) {
                CompoundTag attachmentTag = attachments.getCompound(i);
                Vector2f position = new Vector2f(attachmentTag.getFloat("x"),attachmentTag.getFloat("y"));

                if (attachmentTag.contains("uuid")) { // Read as input
                    this.attachments.add(new Attachment.InputAttachment(position,this,attachmentTag.getInt("index"),attachmentTag.getUUID("uuid")));
                } else {
                    Attachment newAttachment = new Attachment(position, this);
                    this.attachments.add(newAttachment);
                    if (attachmentTag.contains("targetUUID")) { // Read as output with direct target
                        newAttachment.setTargetUUID(attachmentTag.getUUID("targetUUID"));
                    } else if (attachmentTag.contains("segments")) { // Read as output with segments
                        ListTag internalAttachments = attachmentTag.getList("segments",10);
                        Attachment lastAttachment = newAttachment;
                        for (int j = 0; j < internalAttachments.size(); j++) {
                            CompoundTag segmentTag = internalAttachments.getCompound(j);
                            Vector2f segmentPosition = new Vector2f(segmentTag.getFloat("x"),segmentTag.getFloat("y"));
                            Attachment.SegmentAttachment segment = new Attachment.SegmentAttachment(segmentPosition,lastAttachment);
                            lastAttachment.setTarget(segment);
                            if (segmentTag.contains("targetUUID")) {
                                segment.setTargetUUID(segmentTag.getUUID("targetUUID"));
                            }
                            lastAttachment = segment;
                        }

                    }
                }

            }
        }
        return tag;
    }

    private void exploreAttachment(Attachment attachment, List<Attachment> attachments) {
        if (attachments.contains(attachment)) {
            return;
        }
        attachments.add(attachment);
        if (attachment.getTarget() != null && attachment.getTarget() instanceof Attachment.SegmentAttachment) {
            exploreAttachment(attachment.getTarget(), attachments);
        }
    }
}
