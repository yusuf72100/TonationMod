package net.sultan.tonation.capabilities;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.sultan.tonation.capabilities.capabilities.*;

public class CommonProxy {
    public void Init()
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new SharedItemManager()); // Nouveau gestionnaire
        FirstJoinProvider.register();
        MobNearbyProvider.register();
    }
}
