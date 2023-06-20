package net.sultan.tonation.capabilities;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sultan.tonation.capabilities.capabilities.*;

public class CommonProxy {
    public void Init()
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        FirstJoinProvider.register();
    }
}
