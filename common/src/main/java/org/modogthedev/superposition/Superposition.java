package org.modogthedev.superposition;

import com.mojang.logging.LogUtils;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.color.ColorTheme;
import foundry.veil.impl.client.render.pipeline.VeilBloomRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.modogthedev.superposition.client.renderer.ui.SuperpositionUITooltipRenderer;
import org.modogthedev.superposition.core.*;
import org.modogthedev.superposition.networking.SuperpositionMessages;
import org.modogthedev.superposition.system.cable.CableManager;
import org.modogthedev.superposition.system.cable.CablePassthroughManager;
import org.modogthedev.superposition.system.cable.CarabinerManager;
import org.modogthedev.superposition.system.signal.ClientSignalManager;
import org.modogthedev.superposition.system.signal.SignalManager;
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
    public static void preTick(ServerLevel level) {
    }
    public static void tick(ServerLevel level) {
        SignalManager.tick(level);
        CableManager.tick(level);
        CablePassthroughManager.tick(level);
        CarabinerManager.tick(level);
    }
    public static void clientTick(Level level){
        VeilBloomRenderer.enable();
        ClientSignalManager.tick(level);
        CableManager.clientTick(level);
        CablePassthroughManager.tick(level);
        CarabinerManager.tick(level);
        CarabinerManager.clientTick(level);
        SuperpositionUITooltipRenderer.clientTick(level);
    }

    public static void initTheme() {
        SUPERPOSITION_THEME.addColor(new Color(50, 168, 82,200));
        SUPERPOSITION_THEME.addColor(new Color(60, 186, 94,255));
        SUPERPOSITION_THEME.addColor(new Color(44, 150, 72,255));
        SUPERPOSITION_THEME.addColor("background",new Color(50, 168, 82,200));
        SUPERPOSITION_THEME.addColor("topBorder",new Color(60, 186, 94,255));
        SUPERPOSITION_THEME.addColor("bottomBorder",new Color(44, 150, 72,255));
    }

    public static ResourceLocation id(String loc) {
        return ResourceLocation.fromNamespaceAndPath(MODID, loc);
    }
}
