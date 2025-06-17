package net.sultan.tonation.capabilities;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.sultan.tonation.capabilities.capabilities.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = tonation.MODID, name = tonation.NAME, version = tonation.VERSION)
public class tonation
{
    public static tonation instance;
    public static final String MODID = "tonation";
    public static final String NAME = "TonationMod";
    public static final String VERSION = "1.0";
    public static Logger LOGGER = LogManager.getLogger();
    public static SimpleNetworkWrapper network;

    @SidedProxy(clientSide = "net.sultan.tonation.capabilities.ClientProxy", serverSide = "net.sultan.tonation.capabilities.CommonProxy")
    public static CommonProxy commonProxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        commonProxy.Init();
        LOGGER.info("TonationMod initialized");

        //Register network max range 255
        network = NetworkRegistry.INSTANCE.newSimpleChannel("assets");

        int id = 0;
        //Server processing
        network.registerMessage(FirstJoinPacket.ServerHandler.class, FirstJoinPacket.class, id++, Side.SERVER);
        network.registerMessage(WelcomeOverlayPacket.ServerHandler.class, WelcomeOverlayPacket.class, id++, Side.SERVER);
        network.registerMessage(MobNearbyPacket.ServerHandler.class, MobNearbyPacket.class, id++, Side.SERVER);
        network.registerMessage(EmotionOverlayPacket.ServerHandler.class, EmotionOverlayPacket.class, id++, Side.SERVER);
        network.registerMessage(ShareItemPacket.Handler.class, ShareItemPacket.class, id++, Side.SERVER);
        network.registerMessage(RequestSharedItemPacket.Handler.class, RequestSharedItemPacket.class, id++, Side.SERVER);
        network.registerMessage(TriggerAnimationPacket.Handler.class, TriggerAnimationPacket.class, id++, Side.SERVER);

        if(event.getSide() != Side.SERVER)
        {
            //Client processing
            network.registerMessage(FirstJoinPacket.ClientHandler.class, FirstJoinPacket.class, id++, Side.CLIENT);
            network.registerMessage(WelcomeOverlayPacket.ClientHandler.class, WelcomeOverlayPacket.class, id++, Side.CLIENT);
            network.registerMessage(MobNearbyPacket.ClientHandler.class, MobNearbyPacket.class, id++, Side.CLIENT);
            network.registerMessage(EmotionOverlayPacket.ClientHandler.class, EmotionOverlayPacket.class, id++, Side.CLIENT);
            network.registerMessage(TriggerAnimationPacket.Handler.class, TriggerAnimationPacket.class, id++, Side.CLIENT);
        }
        LOGGER = event.getModLog();
    }

    @EventHandler
    public void Init(FMLInitializationEvent event)
    {
        instance = this;
    }
}
