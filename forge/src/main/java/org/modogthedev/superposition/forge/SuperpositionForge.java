package org.modogthedev.superposition.forge;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.modogthedev.superposition.Superposition;
import org.modogthedev.superposition.SuperpositionClient;

@Mod(Superposition.MODID)
public class SuperpositionForge {
    public SuperpositionForge() {
        MinecraftForge.EVENT_BUS.register(this);
        Superposition.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> SuperpositionClient::init);
    }
}
