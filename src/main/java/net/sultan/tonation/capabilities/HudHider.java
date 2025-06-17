package net.sultan.tonation.capabilities;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class HudHider {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        switch (event.getType()) {
            case HEALTH:
                event.setCanceled(true);        // remove health bar
                break;
            case FOOD:
                event.setCanceled(true);        // remove food bar
                break;
            case ARMOR:
                event.setCanceled(true);        // remove armor bar
                break;
            case AIR:
                event.setCanceled(true);        // remove air bar
                break;
            case EXPERIENCE:
                event.setCanceled(true);        // remove experience bar
                break;
            // Add other elements if necessary
        }
    }
}
