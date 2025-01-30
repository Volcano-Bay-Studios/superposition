package org.modogthedev.superposition.system.cable.rope_system;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.SuperpositionConstants;

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
        
        baseConstraints.add(new RopeEdgeConstraint(nodes.get(0), nodes.get(1), connectionWidth));
        for (RopeNode node : nodes) {
            current = node;
            if (doublePrevious != null) {
                baseConstraints.add(new RopeMiddleConstraint(doublePrevious, previous, current, connectionWidth));
            }
            doublePrevious = previous;
            previous = current;
            
        }
        baseConstraints.add(new RopeEdgeConstraint(nodes.get(getNodesCount() - 1), nodes.get(getNodesCount() - 2), connectionWidth));
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
        
        for (RopeNode node : nodes) {
            node.resolveWorldCollisions(level);
        }
        
        for (int i = 0; i < 5; i++) {
            List<RopeConstraint> allConstraints = collectAllConstraints();
            allConstraints.sort((a, b) -> -Double.compare(a.getStress(), b.getStress()));
            
            for (RopeConstraint connection : allConstraints) {
                connection.applyConstraint();
            }
            for (RopeNode node : nodes) {
                node.applyNextPositions();
            }
        }
        
        for (RopeNode node : nodes) {
            node.resolveWorldCollisions(level);
        }
        
        for (RopeNode node : nodes) {
            if (node.prevPosition.distanceTo(node.position) < 0.025f) {
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
    
    private List<RopeConstraint> collectAllConstraints() {
        ArrayList<RopeConstraint> all = new ArrayList<>();
        all.addAll(baseConstraints);
        all.addAll(constraints);
        return all;
    }

    public float calculateOverstretch() {
        float length = 0f;
        for (int i = 0; i < nodes.size()-1; i++) {
            RopeNode node = nodes.get(i);
            RopeNode nextNode = nodes.get(i+1);
            float newLength = (float) node.getPosition().distanceTo(nextNode.getPosition());
            length = Math.max(newLength,length);
        }
        return length- SuperpositionConstants.cableRadius;
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
