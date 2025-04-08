package org.modogthedev.superposition.system.cable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.MeshData;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.Light;
import foundry.veil.api.client.render.light.PointLight;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import foundry.veil.api.client.render.vertex.VertexArray;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.lwjgl.system.NativeResource;
import org.modogthedev.superposition.client.renderer.CableRenderer;
import org.modogthedev.superposition.core.SuperpositionRenderTypes;
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
    private final VertexArray vao;

    private boolean removed = false;

    private List<PointLight> pointLights;

    public CableClientState(Cable cable, RopeSimulation ropeSimulation) {
        this.cable = cable;
        this.ropeSimulation = ropeSimulation;
        this.origin = new Vector3d();
        this.vao = VertexArray.create();
        this.pointLights = null;
    }

    private void updateLight(PointLight light, RopeNode point) {
        float brightness = this.cable.getBrightness();
        Color color = this.cable.getColor();

        light.setPosition(point.getPosition().x, point.getPosition().y, point.getPosition().z);
        light.setBrightness((float) Mth.map(brightness, 1, 200, 0.15, 0.2));
        light.setRadius(Mth.map(brightness, 1, 200, 3, 8));
        light.setColor(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
    }

    private void updateLights() {
        if (this.cable.isEmitsLight() && !removed) {
            if (this.pointLights == null) {
                this.pointLights = new ArrayList<>(this.ropeSimulation.getNodeCount());
            }

            LightRenderer lightRenderer = VeilRenderSystem.renderer().getLightRenderer();
            if (this.pointLights.size() > this.ropeSimulation.getNodeCount()) {
                ListIterator<PointLight> iterator = this.pointLights.listIterator();
                while (iterator.hasNext()) {
                    int i = iterator.nextIndex();
                    PointLight point = iterator.next();
                    if (i >= this.ropeSimulation.getNodeCount()) {
                        lightRenderer.removeLight(point);
                        iterator.remove();
                    }
                }
            } else if (this.pointLights.size() < this.ropeSimulation.getNodeCount()) {
                for (int i = this.pointLights.size(); i < this.ropeSimulation.getNodes().size(); i++) {
                    this.pointLights.add(new PointLight());
                    lightRenderer.addLight(this.pointLights.get(i));
                }
            }

            for (int i = 0; i < this.ropeSimulation.getNodeCount(); i++) {
                this.updateLight(this.pointLights.get(i), this.ropeSimulation.getNode(i));
            }
        }
    }

    public void update(float partialTicks) {
        List<RopeNode> points = this.cable.getPoints();
        if (points.isEmpty()) {
            this.vao.setIndexCount(0, VertexArray.IndexType.BYTE);
            return;
        }

        Vec3 nodePos = points.getFirst().getPosition();
        this.origin.set(nodePos.x, nodePos.y, nodePos.z);

        RenderType renderType = SuperpositionRenderTypes.cable();
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(renderType.mode(), renderType.format());
        CableRenderer.renderCable(this.cable, this, builder, Minecraft.getInstance().level, this.cable.isSleeping() ? 1.0F : partialTicks);

        MeshData data = builder.build();
        if (data != null) {
            this.vao.bind();
            this.vao.upload(data, VertexArray.DrawUsage.STATIC);
        } else {
            this.vao.setIndexCount(0, VertexArray.IndexType.BYTE);
        }

        this.updateLights();
    }

    public Vector3dc getOrigin() {
        return this.origin;
    }

    @Override
    public void free() {
        this.vao.free();
    }

    public void remove() {
        LightRenderer lightRenderer = VeilRenderSystem.renderer().getLightRenderer();
        if (pointLights != null) {
            for (Light light : pointLights) {
                lightRenderer.removeLight(light);
            }
            pointLights.clear();
        }
        removed = true;
    }

    public void render(ShaderInstance shader, Matrix4f modelView, Matrix4f projection) {
        shader.setDefaultUniforms(
                SuperpositionRenderTypes.cable().mode(),
                modelView,
                projection,
                Minecraft.getInstance().getWindow());
        this.vao.bind();
        this.vao.draw();
    }
}
