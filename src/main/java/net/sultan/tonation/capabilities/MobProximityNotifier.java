package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.List;

public class MobProximityNotifier {

    private int cooldown = 0;

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;
        double radius = 10.0;

        if (player == null || mc.world == null) return;

        // Rafraîchissement toutes les 20 ticks (1 seconde)
        if (cooldown > 0) {
            cooldown--;
            return;
        }

        List<EntityLivingBase> nearbyEntities = mc.world.getEntitiesWithinAABB(EntityLivingBase.class,
                player.getEntityBoundingBox().grow(radius),
                entity -> entity != null && !entity.getUniqueID().equals(player.getUniqueID()));

        for (EntityLivingBase entity : nearbyEntities) {
            if (entity instanceof EntityPlayer) {
                player.sendMessage(new TextComponentString("§aJoueur détecté à proximité !"));
            } else if (entity instanceof EntityVillager) {
                player.sendMessage(new TextComponentString("§cVillageois détecté à proximité : " + entity.getName()));
            } else {
                player.sendMessage(new TextComponentString("§cMob détecté à proximité : " + entity.getName()));
            }
        }

        cooldown = 10;
    }
}
