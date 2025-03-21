package org.modogthedev.superposition.system.cable;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.system.NativeResource;
import org.modogthedev.superposition.system.cable.rope_system.RopeNode;
import org.modogthedev.superposition.system.cable.rope_system.RopeSimulation;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class CableClientState implements NativeResource {

    private final Cable cable;
    private final RopeSimulation ropeSimulation;
    private final Vector3d origin;

    private List<PointLight> pointLights;

    public CableClientState(Cable cable, RopeSimulation ropeSimulation) {
        this.cable = cable;
        this.ropeSimulation = ropeSimulation;
        this.origin = new Vector3d();
    }

    private void updateLight(PointLight light, RopeNode point) {
        float brightness = this.cable.getBrightness();
        Color color = this.cable.getColor();

        light.setPosition(point.getPosition().x, point.getPosition().y, point.getPosition().z);
        light.setBrightness((float) Mth.map(brightness, 1, 200, 0.15, 0.2));
        light.setRadius(Mth.map(brightness, 1, 200, 3, 8));
        light.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    public void update() {
        List<RopeNode> points = this.cable.getPoints();
        if (points.isEmpty()) {
            return;
        }

        Vec3 nodePos = points.getFirst().getPosition();
        this.origin.set(nodePos.x, nodePos.y, nodePos.z);
    }

    public void updateLights(float partialTicks) {
        if (this.cable.isEmitsLight()) {
            if (this.pointLights == null) {
                this.pointLights = new ArrayList<>();
            }

            LightRenderer lightRenderer = VeilRenderSystem.renderer().getLightRenderer();
            if (this.pointLights.size() > this.ropeSimulation.getNodesCount()) {
                ListIterator<PointLight> iterator = this.pointLights.listIterator();
                while (iterator.hasNext()) {
                    int i = iterator.nextIndex();
                    PointLight point = iterator.next();
                    if (i >= this.ropeSimulation.getNodesCount()) {
                        lightRenderer.removeLight(point);
                        iterator.remove();
                    }
                }
            } else if (this.pointLights.size() < this.ropeSimulation.getNodesCount()) {
                for (int i = this.pointLights.size(); i < this.ropeSimulation.getNodes().size(); i++) {
                    this.pointLights.add(new PointLight());
                    lightRenderer.addLight(this.pointLights.get(i));
                }
            }

            for (int i = 0; i < this.ropeSimulation.getNodesCount(); i++) {
                this.updateLight(this.pointLights.get(i), this.ropeSimulation.getNode(i));
            }
        }
    }

    public Vector3dc getOrigin() {
        return this.origin;
    }

    @Override
    public void free() {

    }
}
