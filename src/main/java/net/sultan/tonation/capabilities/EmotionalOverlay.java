package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EmotionalOverlay {
    private static String currentEmotion;

    public EmotionalOverlay(String Emotion) {
        currentEmotion = Emotion;
    }

    public static void setEmotion(String emotion) {
        currentEmotion = emotion;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (!Minecraft.getMinecraft().player.isEntityAlive()) return;

        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc);

        // Position juste à droite des coeurs de vie
        int x = 10 + 81; // 10 px marge + largeur approximative des coeurs
        int y = res.getScaledHeight() - 39;

        mc.getTextureManager().bindTexture(new ResourceLocation("tonation", "Emotional/" + currentEmotion + ".png"));
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, 32, 32, 32, 32);
    }
}
