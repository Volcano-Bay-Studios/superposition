package org.modogthedev.superposition.system.cable;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.modogthedev.superposition.util.Mth;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CableClipResult {
    private HashMap<Cable, List<Cable.Point>> cablePointMap = new HashMap<>();
    private final Level level;
    private final Vec3 sourcePos;
    private final float collectRange;

    public CableClipResult(Vec3 pos, float collectRange, Level level) {
        this.sourcePos = pos;
        this.collectRange = collectRange;
        this.level = level;
        collectPoints();
    }

    private void collectPoints() {
        for (Cable cable : CableManager.getLevelCables(level)) {
            if (cable.getPoints().get(0).getPosition().distanceTo(sourcePos) < cable.getPoints().size() + collectRange) {
                cablePointMap.put(cable, new ArrayList<>());
                for (Cable.Point point : cable.getPoints()) {
                    if (point.getPosition().distanceTo(sourcePos) < collectRange) {
                        cablePointMap.get(cable).add(point);
                    }
                }
            }
        }
    }

    public HashMap<Cable, List<Cable.Point>> getCablePointMap() {
        return cablePointMap;
    }

    public List<Pair<Cable, Cable.Point>> rayCast(Vec3 toPos, float range) {
        List<Pair<Cable, Cable.Point>> pointPairList = new ArrayList<>();
        for (float delta = 0; delta < sourcePos.distanceTo(toPos); delta += range) {
            Vec3 stepPos = Mth.lerpVec3(sourcePos,toPos,Mth.getFromRange((float) sourcePos.distanceTo(toPos),0,1,0,delta));
            for (Cable cable : cablePointMap.keySet()) {
                for (Cable.Point point : cablePointMap.get(cable)) {
                    if (point.getPosition().distanceTo(stepPos) < range) {
                        pointPairList.add(new Pair<>(cable,point));
                    }
                }
            }
        }
        return pointPairList;
    }
}
