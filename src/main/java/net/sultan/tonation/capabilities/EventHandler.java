package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
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
        tonation.LOGGER.info("attach working! ");
        event.addCapability(FIRST_JOIN_CAP, new FirstJoinProvider((EntityPlayer)event.getObject()));
        event.addCapability(MOB_NEARBY_CAP, new MobNearbyProvider((EntityPlayer)event.getObject()));
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        IFirstJoin fj = event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null);
        fj.sync(event.player);
        IMobNearby cap = event.player.getCapability(MobNearbyStorage.MOB_NEARBY_CAP, null);
        cap.sync(event.player);
        tonation.LOGGER.info("Logout!");
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
            //affichage du gui WelcomeOverlay au client
            tonation.network.sendTo(new WelcomeOverlayPacket("welcome_gui"), (EntityPlayerMP) event.player);
            return;
        }
        event.player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null).setFirstJoin();
    }

    @SubscribeEvent @SideOnly(Side.SERVER)
    public static void onPlayerTick(net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent event)
    {
        // Executing every 30 secondes (600 ticks)
        if (event.player.ticksExisted % 600 != 0) {
            return;
        }

        IMobNearby cap = event.player.getCapability(MobNearbyStorage.MOB_NEARBY_CAP, null);
        cap.sync(event.player);

        if (cap == null) {
            tonation.LOGGER.info("cap marche pas! ");
            return;
        }

        // Emotional Overlay
        double radius = 10.0;
        // friendly mobs
        List<EntityLivingBase> friendlyEntities = event.player.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                event.player.getEntityBoundingBox().grow(radius),
                entity ->
                        !(entity instanceof IMob) &&    // Exclut les mobs hostiles
                        !(entity instanceof EntityMob)  // Exclut aussi EntityMob
        );

        List<EntityLivingBase> hostileEntities = event.player.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                event.player.getEntityBoundingBox().grow(radius),
                entity ->
                        (entity instanceof IMob) ||    // Inclut les mobs hostiles
                        (entity instanceof EntityMob)  // Inclut aussi EntityMob
        );

        int friendlyMods = friendlyEntities.size();
        int hostileMobs = hostileEntities.size();

        if (!isPlayerInCreative(event.player)) {        // do nothing in creative mode
            if (friendlyMods > hostileMobs) {
                if (cap.getTimer() < 100) {
                    cap.setTimer(cap.getTimer() + 5);
                }
            } else {
                if (cap.getTimer() > 0) {
                    cap.setTimer(cap.getTimer() - 5);
                }
            }
        }

        // Sending datas to client
        if (event.player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) event.player;

            // Sync capability
            tonation.network.sendTo(new MobNearbyPacket(cap.getTimer()), playerMP);

            // managing overlay
            if (cap.getTimer() >= 80 && cap.getTimer() <= 100) {
                tonation.network.sendTo(new EmotionOverlayPacket("high"), playerMP);
            } else if (cap.getTimer() >= 40 && cap.getTimer() < 80) {
                tonation.network.sendTo(new EmotionOverlayPacket("medium"), playerMP);
            } else {
                tonation.network.sendTo(new EmotionOverlayPacket("low"), playerMP);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        // Check if player
        if (event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            IMobNearby cap = player.getCapability(MobNearbyStorage.MOB_NEARBY_CAP, null);

            if (cap == null) {
                tonation.LOGGER.info("cap marche pas! ");
                return;
            }

            // Player taking damage
            float damage = event.getAmount();

            if (cap.getTimer() > 0) {
                cap.setTimer((int) (cap.getTimer() - (damage * 2)));
            }
        }
    }

    // Check if player is in creative mode
    public static boolean isPlayerInCreative(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            GameType gameType = playerMP.interactionManager.getGameType();
            return gameType == GameType.CREATIVE;
        }
        return false;
    }
}