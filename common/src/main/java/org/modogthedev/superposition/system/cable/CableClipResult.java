package org.modogthedev.superposition.system.cable;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.core.SuperpositionConstants;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;
import org.modogthedev.superposition.util.SuperpositionMth;
import oshi.util.tuples.Pair;

import java.util.*;

// TODO add spacial mapping + make this static
public class CableClipResult {

    private final HashMap<Cable, List<RopeNode>> cablePointMap = new HashMap<>();
    private final Level level;
    private final Vec3 sourcePos;
    private final float collectRange;

    public CableClipResult(Vec3 pos, float collectRange, Level level) {
        this.sourcePos = pos;
        this.collectRange = collectRange;
        this.level = level;
        this.collectPoints();
    }

    private void collectPoints() {
        for (Cable cable : CableManager.getLevelCables(level)) {
            if (cable.getPoints().getFirst().getPosition().distanceTo(sourcePos) < cable.getPoints().size() + collectRange) {
                cablePointMap.put(cable, new ArrayList<>());
                for (RopeNode point : cable.getPoints()) {
                    if (point.getPosition().distanceTo(sourcePos) < collectRange) {
                        cablePointMap.get(cable).add(point);
                    }
                }
            }
        }
    }

    public Pair<Cable, RopeNode> rayCastForClosest(Vec3 toPos, float range, boolean perferEnd) {
        List<Pair<Cable, RopeNode>> rayCast = this.rayCast(toPos, range);
        List<Pair<Float, RopeNode>> distancePointPairMap = new ArrayList<>();

        assert range > 0;

        for (Pair<Cable, RopeNode> cablePointPair : rayCast) {
            distancePointPairMap.add(new Pair<>(999f, cablePointPair.getB()));
        }
        for (float delta = 0; delta < sourcePos.distanceTo(toPos); delta += range) {
            Vec3 stepPos = SuperpositionMth.lerpVec3(sourcePos, toPos, SuperpositionMth.getFromRange((float) sourcePos.distanceTo(toPos), 0, 1, 0, delta));
            int i = 0;
            for (Pair<Cable, RopeNode> cablePointPair : rayCast) {
                boolean isAnEndPoint = cablePointPair.getA().getPoints().get(cablePointPair.getA().getPoints().size() - 1).equals(cablePointPair.getB()) || cablePointPair.getA().getPoints().get(0).equals(cablePointPair.getB());
                float distance = (float) cablePointPair.getB().getPosition().distanceTo(stepPos) - ((isAnEndPoint & perferEnd) ? SuperpositionConstants.endPreference : 0f);
                float storedDistance = distancePointPairMap.get(i).getA();
                if (distance < storedDistance) {
                    distancePointPairMap.set(i, new Pair<>(distance, cablePointPair.getB()));
                }
                i++;
            }
        }
        float closetPointDistance = 999f;
        int closestPointIndex = 0;
        for (int i = 0; i < rayCast.size(); i++) {
            float distance = distancePointPairMap.get(i).getA();
            if (distance < closetPointDistance) {
                closetPointDistance = distance;
                closestPointIndex = i;
            }
        }
        if (!rayCast.isEmpty()) {
            return rayCast.get(closestPointIndex);
        }
        return null;
    }

    public Pair<Cable, RopeNode> filteredRayCastForClosest(Vec3 toPos, float range, UUID uuid) {
        List<Pair<Cable, RopeNode>> rayCast = this.rayCast(toPos, range);
        List<Pair<Float, RopeNode>> distancePointPairMap = new ArrayList<>();
        ListIterator<Pair<Cable, RopeNode>> iterator = rayCast.listIterator();
        while (iterator.hasNext()) {
            if (!iterator.next().getA().getId().equals(uuid)) {
                iterator.remove();
            }
        }
        for (Pair<Cable, RopeNode> cablePointPair : rayCast) {
            distancePointPairMap.add(new Pair<>(999f, cablePointPair.getB()));
        }
        for (float delta = 0; delta < sourcePos.distanceTo(toPos); delta += range) {
            Vec3 stepPos = SuperpositionMth.lerpVec3(sourcePos, toPos, SuperpositionMth.getFromRange((float) sourcePos.distanceTo(toPos), 0, 1, 0, delta));
            int i = 0;
            for (Pair<Cable, RopeNode> cablePointPair : rayCast) {
                boolean isAnEndPoint = cablePointPair.getA().getPoints().get(cablePointPair.getA().getPoints().size() - 1).equals(cablePointPair.getB()) || cablePointPair.getA().getPoints().get(0).equals(cablePointPair.getB());
                float distance = (float) cablePointPair.getB().getPosition().distanceTo(stepPos) - (isAnEndPoint ? SuperpositionConstants.endPreference : 0f);
                float storedDistance = distancePointPairMap.get(i).getA();
                if (distance < storedDistance) {
                    distancePointPairMap.set(i, new Pair<>(distance, cablePointPair.getB()));
                }
                i++;
            }
        }
        float closetPointDistance = 999f;
        int closestPointIndex = 0;
        for (int i = 0; i < rayCast.size(); i++) {
            float distance = distancePointPairMap.get(i).getA();
            if (distance < closetPointDistance) {
                closetPointDistance = distance;
                closestPointIndex = i;
            }
        }
        if (!rayCast.isEmpty()) {
            return rayCast.get(closestPointIndex);
        }
        return null;
    }

    public List<Pair<Cable, RopeNode>> rayCast(Vec3 toPos, float range) {
        List<Pair<Cable, RopeNode>> pointPairList = new ArrayList<>();
        for (float delta = 0; delta < sourcePos.distanceTo(toPos); delta += range) {
            Vec3 stepPos = SuperpositionMth.lerpVec3(sourcePos, toPos, SuperpositionMth.getFromRange((float) sourcePos.distanceTo(toPos), 0, 1, 0, delta));
            for (Cable cable : cablePointMap.keySet()) {
                for (RopeNode point : cablePointMap.get(cable)) {
                    if (point.getPosition().distanceTo(stepPos) < range) {
                        pointPairList.add(new Pair<>(cable, point));
                    }
                }
            }
        }
        return pointPairList;
    }
}
