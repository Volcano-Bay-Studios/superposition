package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RopeSimulation {
    
    float connectionWidth;
    
    List<RopeNode> nodes = new ArrayList<>();
    List<RopeConstraint> baseConstraints = new ArrayList<>();
    List<RopeConstraint> constraints = new ArrayList<>();
    
    int sleepTime = 0;
    
    public RopeSimulation(float connectionWidth) {
        this.connectionWidth = connectionWidth;
    }

    public void createRope(int count, Vec3 from, Vec3 to) {
        nodes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            addNode(new RopeNode(from.lerp(to, (double) i / count)));
        }
        recalculateBaseRopeConstraints();
    }

    public void addNode(RopeNode ropeNode) {
        nodes.add(ropeNode);
        ropeNode.simulation = this;
    }

    public void addNode(int index, RopeNode ropeNode) {
        nodes.add(index, ropeNode);
        ropeNode.simulation = this;
    }

    public void recalculateBaseRopeConstraints() {
        baseConstraints = new ArrayList<>();

        RopeNode doublePrevious = null;
        RopeNode previous = null;
        RopeNode current;

        for (RopeNode node : nodes) {
            current = node;
            if (doublePrevious != null) {
                baseConstraints.add(new RopeMiddleConstraint(doublePrevious, previous, current, connectionWidth));
            }
            doublePrevious = previous;
            previous = current;
        }
    }

    public void resizeRope(int count) {
        int oldCount = nodes.size();

        if (oldCount > count) {
            nodes = nodes.subList(0, count);
        } else {
            Vec3 insertDirection = nodes.get(oldCount - 1).position.subtract(nodes.get(oldCount - 2).position);
            Vec3 insertPos = nodes.get(oldCount - 1).position.add(insertDirection);
            for (int i = oldCount; i < count; i++) {
                RopeNode node = new RopeNode(insertPos);
                addNode(node);
                insertPos = insertPos.add(insertDirection);
            }
        }

        recalculateBaseRopeConstraints();
    }


    public void simulate(Level level) {
        
        for (RopeNode node : nodes) {
            Vec3 velocity = Vec3.ZERO;
            if (!node.isFixed()) {
                velocity = node.getPosition().subtract(node.prevPosition);

                velocity = velocity.add(0, 0.5 * -9.8 / 40, 0);
                velocity = velocity.scale(0.9f * (velocity.length() < 1e-2 ? 0.1 : 1.0));
            }
            node.prevPosition = node.position;
            node.position = node.position.add(velocity);
        }

        List<RopeConstraint> constraints = collectAllConstraints();
        for (int i = 0; i < constraints.size(); i++) {
            constraints.get(i).applyConstraint();
        }
        for (int i = constraints.size()-1; i >= 0; i--) {
            constraints.get(i).applyConstraint();
        }

        for (RopeNode node : nodes) {
            node.resolveWorldCollisions(level);
        }
        
        for (int i = 0; i < 5; i++) {
            processLength(true);
            for (RopeNode node : nodes) {
                node.resolveWorldCollisions(level);
            }

            processLength(false);
            for (RopeNode node : nodes) {
                node.resolveWorldCollisions(level);
            }

        }

        for (RopeNode node : nodes) {
            node.resolveWorldCollisions(level);
        }

        for (RopeNode node : nodes) {
            if (node.prevPosition.distanceTo(node.position) < 0.0025f) {
                node.position = node.prevPosition;
            }
        }

        boolean shouldSleep = true;
        for (RopeNode node : nodes) {
            Vec3 movedLastTick = node.getPrevPosition().subtract(node.getPosition());
            if (movedLastTick.lengthSqr() > 1e-9) {
                shouldSleep = false;
                break;
            }
        }

        if (shouldSleep) {
            sleepTime++;
        } else {
            sleepTime = 0;
        }
    }

    private void processLength(boolean forwards) {
        for (int j = forwards ? 0 : nodes.size() -1; forwards ? (j < nodes.size() -1) : (j > 1); j += forwards ? 1 : -1) {
            RopeNode node = nodes.get(j);
            RopeNode nextNode = nodes.get(j + (forwards ? 1 : -1));

            double dist = node.position.distanceTo(nextNode.position);
            if (dist > 20) {
                Vec3 midpoint = node.position.lerp(nextNode.position, 0.5);
                node.position = midpoint;
                nextNode.position = midpoint;
            } else {
                double change = (dist - connectionWidth) / 4;

                node.position = node.position.add(nextNode.position.subtract(node.position).normalize().scale(change));
                nextNode.position = nextNode.position.add(node.position.subtract(nextNode.position).normalize().scale(change));
            }
        }
    }

    public void updatePrevRenderPos() {
        for (RopeNode node : nodes) {
            node.prevRenderPosition = node.position;
        }
    }

    private List<RopeConstraint> collectAllConstraints() {
        ArrayList<RopeConstraint> all = new ArrayList<>();
        all.addAll(baseConstraints);
        all.addAll(constraints);
        return all;
    }

    public float calculateOverstretch(RopeNode origin) {
        int originIndex = nodes.indexOf(origin);

        int walkedNodes = 0;
        double walkedNodesLength = 0;
        double maxWalkedNodeOverstretch = 0;

        for (int i = originIndex; i < nodes.size()-1; i++) {
            RopeNode current = nodes.get(i);
            RopeNode next = nodes.get(i+1);

            walkedNodes++;
            double dist = current.position.distanceTo(next.position);
            walkedNodesLength += dist;
            maxWalkedNodeOverstretch = Math.max(dist, maxWalkedNodeOverstretch);

            if (next.anchor != null) break;
        }
        for (int i = originIndex; i > 1; i--) {
            RopeNode current = nodes.get(i);
            RopeNode next = nodes.get(i-1);

            walkedNodes++;
            double dist = current.position.distanceTo(next.position);
            walkedNodesLength += dist;
            maxWalkedNodeOverstretch = Math.max(dist, maxWalkedNodeOverstretch);

            if (next.anchor != null) break;
        }

        return walkedNodes == 0 ? 0 : (float) Math.max(maxWalkedNodeOverstretch / 10f, (walkedNodesLength / walkedNodes) - connectionWidth);
    }

    public List<RopeConstraint> getConstraints() {
        return constraints;
    }

    public void removeConstraint(RopeConstraint anchor) {
        constraints.remove(anchor);
    }

    public void addConstraint(RopeConstraint constraint) {
        constraints.add(constraint);
    }

    public RopeNode getNode(int index) {
        if (index < 0 || nodes.size() -1 < index) {
            return null;
        }
        return nodes.get(index);
    }

    public int getNodesCount() {
        return nodes.size();
    }

    public List<RopeNode> getNodes() {
        return nodes;
    }

    public void removeAllConstraints() {
        constraints = new ArrayList<>();
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void invalidateSleepTime() {
        sleepTime = 0;
    }

    public boolean isSleeping() {
        return sleepTime > 20;
    }

}
