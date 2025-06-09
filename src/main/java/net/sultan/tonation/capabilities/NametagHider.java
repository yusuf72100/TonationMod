package net.sultan.tonation.capabilities;

import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;

@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public class NametagHider {

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Specials.Pre event) {
        if (event.getEntity() instanceof EntityPlayer) {
            // Hide nametag
            event.setCanceled(true);
        }
    }
}
