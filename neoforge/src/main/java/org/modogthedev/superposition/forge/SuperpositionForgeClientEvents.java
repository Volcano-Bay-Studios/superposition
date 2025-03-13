package org.modogthedev.superposition.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.sound.PlayStreamingSourceEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.SuperpositionClient;
import org.modogthedev.superposition.system.sound.SpeakerSoundInstance;

@EventBusSubscriber(modid = Superposition.MODID, value = Dist.CLIENT)
public class SuperpositionForgeClientEvents {

    @SubscribeEvent
    public static void onClientTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        Superposition.clientTick(level);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Superposition.clientAlwaysTick(Minecraft.getInstance());
    }

    @SubscribeEvent
    public static void onPreTick(LevelTickEvent.Pre event) {
        Superposition.preTick(event.getLevel());
    }

    @SubscribeEvent
    public static void onLevelLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        Superposition.playerLeaveEvent(event.getEntity().level());
    }

    @SubscribeEvent
    public static void playStreaming(PlayStreamingSourceEvent event){
        if (!(event.getSound() instanceof SpeakerSoundInstance sound) || sound.getStream() == null) return;
        SuperpositionClient.playStreaming(event.getEngine(), event.getChannel(), sound.getStream());
    }
}
