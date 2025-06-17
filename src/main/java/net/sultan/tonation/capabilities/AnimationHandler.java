package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class AnimationHandler {
    private static final Map<UUID, AnimationData> activeAnimations = new HashMap<>();

    public static void triggerShareAnimation(EntityPlayer player, ItemStack item) {
        Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Animation triggered for " + player.getName()));
        AnimationData data = new AnimationData(item, 60); // 3 secondes à 20 TPS
        activeAnimations.put(player.getUniqueID(), data);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Iterator<Map.Entry<UUID, AnimationData>> iterator = activeAnimations.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<UUID, AnimationData> entry = iterator.next();
                AnimationData data = entry.getValue();
                data.ticksRemaining--;

                if (data.ticksRemaining <= 0) {
                    iterator.remove();
                }
            }
        }
    }

    public static boolean isPlayerAnimating(EntityPlayer player) {
        return activeAnimations.containsKey(player.getUniqueID());
    }

    public static ItemStack getAnimationItem(EntityPlayer player) {
        AnimationData data = activeAnimations.get(player.getUniqueID());
        return data != null ? data.item : ItemStack.EMPTY;
    }

    public static float getAnimationProgress(EntityPlayer player) {
        AnimationData data = activeAnimations.get(player.getUniqueID());
        if (data == null) return 0.0F;
        return Math.min(1.0F, (60.0F - (float) data.ticksRemaining) / 20.0F);
    }

    private static class AnimationData {
        final ItemStack item;
        int ticksRemaining;

        AnimationData(ItemStack item, int duration) {
            this.item = item;
            this.ticksRemaining = duration;
        }
    }
}
