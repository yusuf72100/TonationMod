package net.sultan.tonation.capabilities;

import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {
    @Override
    public void Init() {
        super.Init();
        MinecraftForge.EVENT_BUS.register(EmotionalOverlay.class);
    }
}
