package net.sultan.tonation.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sultan.tonation.capabilities.capabilities.FirstJoinProvider;
import net.sultan.tonation.capabilities.capabilities.FirstJoinStorage;
import net.sultan.tonation.capabilities.capabilities.IFirstJoin;
import net.sultan.tonation.capabilities.capabilities.WelcomeOverlayPacket;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class EventHandler {
    public static final ResourceLocation FIRST_JOIN_CAP = new ResourceLocation(tonation.MODID, "first_join_cap");

    @SubscribeEvent @SideOnly(Side.SERVER)
    public void attachCapability(@Nonnull AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof EntityPlayer)) return;
        tonation.LOGGER.info("attach marche bien! ");
        event.addCapability(FIRST_JOIN_CAP, new FirstJoinProvider((EntityPlayer)event.getObject()));
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        IFirstJoin fj = event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null);
        tonation.LOGGER.info("Deconnection! ");
        fj.sync(event.player);
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if(!event.player.world.isRemote)
        {
            event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null).sync(event.player);
        }
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        IFirstJoin fj = event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null);
        fj.sync(event.player);

        if(fj.isFirstConnection() == 1)
        {
            String message = String.format("Here is your first connection!");
            event.player.sendMessage(new TextComponentString(message));
            //affichage du gui WelcomeOverlay au client
            tonation.network.sendTo(new WelcomeOverlayPacket("welcome_gui"), (EntityPlayerMP) event.player);
            return;
        }
        event.player.sendMessage(new TextComponentString("Hello back bud!"));
        event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null).setFirstJoin();
    }
}
