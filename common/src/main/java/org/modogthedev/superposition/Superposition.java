package org.modogthedev.superposition;

import com.mojang.logging.LogUtils;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.ColorTheme;
import foundry.veil.impl.client.render.pipeline.VeilBloomRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.client.renderer.SuperpositionLightSystem;
import org.modogthedev.superposition.client.renderer.ui.SuperpositionUITooltipRenderer;
import org.modogthedev.superposition.core.*;
import org.modogthedev.superposition.networking.SuperpositionMessages;
import org.modogthedev.superposition.persistent.CableSavedData;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.CablePassthroughManager;
import org.modogthedev.superposition.system.cable.CarabinerManager;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.SignalManager;
import org.modogthedev.superposition.system.sound.ClientAudioManager;
import org.modogthedev.superposition.system.world.RedstoneWorld;
import org.slf4j.Logger;

public class Superposition {

    public static final String MODID = "superposition";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final ColorTheme SUPERPOSITION_THEME = new ColorTheme();
    public static boolean DEBUG = false;

    public static void init() {
        LOGGER.info("Superposition is initializing.");

        initTheme();
        SuperpositionBlocks.bootstrap();
        SuperpositionFilters.bootstrap();
        SuperpositionCards.bootstrap();
        SuperpositionBlockEntities.bootstrap();
        SuperpositionItems.bootstrap();
        SuperpositionTabs.bootstrap();
        SuperpositionSounds.bootstrap();
        SuperpositionMessages.register();
        LOGGER.info("Superposition has been initialized.");
    }
    public static void preTick(Level level) {
        if (level.isClientSide) {
            RedstoneWorld.clientTick(level);
        } else {
            RedstoneWorld.tick(level);
        }
    }

    public static void tick(ServerLevel level) {
        SignalManager.tick(level);
        CableManager.tick(level);
        CableSavedData.get(level);
        CablePassthroughManager.tick(level);
        CarabinerManager.tick(level);
    }

    public static void clientTick(Level level) {
        VeilBloomRenderer.enable();
        ClientSignalManager.tick(level);
        CableManager.clientTick(level);
        CablePassthroughManager.tick(level);
        CarabinerManager.tick(level);
        CarabinerManager.clientTick(level);
        SuperpositionUITooltipRenderer.clientTick(level);
        ClientAudioManager.tick(level);
        SuperpositionLightSystem.tick(level);
    }

    public static void clientAlwaysTick(Minecraft client) {
        if (client.level == null) {
            CableManager.wipeClientData();
        }
    }

    public static void playerLeaveEvent(Level level) {
        if (level.isClientSide) {
            CableManager.wipeClientData();
        }
    }

    public static void loadLevel(ServerLevel level) {
        CableSavedData.get(level);
    }

    public static void initTheme() {
        Color background = new Color().setInt(50, 168, 82, 150);
        Color borderTop = new Color().setInt(60, 186, 94, 255);
        Color borderBottom = new Color().setInt(44, 150, 72, 255);
        SUPERPOSITION_THEME.addColor(background);
        SUPERPOSITION_THEME.addColor(borderTop);
        SUPERPOSITION_THEME.addColor(borderBottom);
        SUPERPOSITION_THEME.addColor("background", background);
        SUPERPOSITION_THEME.addColor("topBorder", borderTop);
        SUPERPOSITION_THEME.addColor("bottomBorder", borderBottom);
    }

    public static ResourceLocation id(String loc) {
        return ResourceLocation.fromNamespaceAndPath(MODID, loc);
    }
}
