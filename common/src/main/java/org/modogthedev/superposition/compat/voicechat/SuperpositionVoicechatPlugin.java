package org.modogthedev.superposition.compat.voicechat;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.events.ClientVoicechatConnectionEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.system.sound.ClientMicrophone;
import org.modogthedev.superposition.system.stream.StreamManager;

/**
 * Sends what players say through Simple Voice Chat to the microphones they are in range of.
 */
@ForgeVoicechatPlugin
public class SuperpositionVoicechatPlugin implements VoicechatPlugin {

    @Override
    public String getPluginId() {
        return Superposition.MODID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophonePacket);
        registration.registerEvent(ClientVoicechatConnectionEvent.class, this::onClientConnection);
    }

    private void onMicrophonePacket(MicrophonePacketEvent event) {
        VoicechatConnection connection = event.getSenderConnection();
        if (connection == null) {
            return;
        }
        StreamManager.pushFrame(connection.getPlayer().getUuid(), event.getPacket().getOpusEncodedData());
    }

    private void onClientConnection(ClientVoicechatConnectionEvent event) {
        ClientMicrophone.voicechatConnected = event.isConnected();
    }
}
