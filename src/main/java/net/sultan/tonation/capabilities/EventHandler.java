package net.sultan.tonation.capabilities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.sultan.tonation.capabilities.capabilities.*;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber
public class EventHandler {
    public static final ResourceLocation FIRST_JOIN_CAP = new ResourceLocation(tonation.MODID, "first_join_cap");
    public static final ResourceLocation MOB_NEARBY_CAP = new ResourceLocation(tonation.MODID, "mob_nearby_cap");

    @SubscribeEvent
    public static void attachCapability(@Nonnull AttachCapabilitiesEvent<Entity> event)
    {
        if (!(event.getObject() instanceof EntityPlayer)) return;
        tonation.LOGGER.info("attach marche bien! ");
        event.addCapability(FIRST_JOIN_CAP, new FirstJoinProvider((EntityPlayer)event.getObject()));
        event.addCapability(MOB_NEARBY_CAP, new MobNearbyProvider((EntityPlayer)event.getObject()));
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        IFirstJoin fj = event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null);
        fj.sync(event.player);
        tonation.LOGGER.info("Deconnection!");
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if(!event.player.world.isRemote)
        {
            event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null).sync(event.player);
            event.player.getCapability(MobNearbyStorage.MOB_NEARBY_CAP, null).sync(event.player);
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
        event.player.sendMessage(new TextComponentString("Welcome back bud!"));
        event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null).setFirstJoin();
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerTick(net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent event)
    {
        IMobNearby cap = event.player.getCapability(MobNearbyStorage.MOB_NEARBY_CAP, null);

        if (cap == null) {
            tonation.LOGGER.info("cap marche pas! ");
            return;
        }

        double radius = 10.0;
        List<EntityLivingBase> entities = event.player.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                event.player.getEntityBoundingBox().grow(radius),
                entity -> !(entity instanceof EntityPlayer)
        );

        boolean foundMob = !entities.isEmpty();

        if (foundMob) {
            cap.setTimer(10);
        } else {
            if (cap.getTimer() > 0) {
                cap.setTimer(cap.getTimer() - 1);
            }
        }

        // Maintenant que le timer est à jour, on peut envoyer les infos au client
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

            // Sync capability (si jamais tu veux l'utiliser côté client)
            tonation.network.sendTo(new MobNearbyPacket(cap.getTimer()), playerMP);

            // Envoi de l'overlay en fonction de la valeur actuelle du timer
            if (cap.getTimer() == 10) {
                playerMP.sendMessage(new TextComponentString("Emotion : high"));
                tonation.network.sendTo(new EmotionOverlayPacket("high"), playerMP);
            } else {
                playerMP.sendMessage(new TextComponentString("Emotion : low"));
                tonation.network.sendTo(new EmotionOverlayPacket("low"), playerMP);
            }
        }
    }

}
