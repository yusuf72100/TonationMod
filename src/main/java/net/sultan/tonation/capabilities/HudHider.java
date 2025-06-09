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
                event.setCanceled(true); // Enlève les cœurs de vie
                break;
            case FOOD:
                event.setCanceled(true); // Enlève la barre de faim
                break;
            case ARMOR:
                //event.setCanceled(true); // Enlève les points d'armure
                break;
            case AIR:
                event.setCanceled(true); // Enlève les bulles d'air
                break;
            case EXPERIENCE:
                event.setCanceled(true); // Enlève la barre d'XP
                break;
            // Ajoute d'autres éléments si nécessaire
        }
    }
}
