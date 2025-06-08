package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EmotionalOverlay {
    private static String currentEmotion;

    public EmotionalOverlay(String Emotion) {
        currentEmotion = Emotion;
    }

    public static void setEmotion(String Emotion) {
        currentEmotion = Emotion;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            renderImage();
        }
    }

    private static void renderImage() {
        ResourceLocation imageTexture = new ResourceLocation(tonation.MODID,"textures/emotional/" + currentEmotion + ".png");
        Minecraft mc = Minecraft.getMinecraft();

        // Screen dimensions
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();

        // Texture binding
        mc.getTextureManager().bindTexture(imageTexture);

        // Transparency ON
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Bottom right corner
        int width = 16; // Largeur
        int height = 16; // Hauteur
        int x = screenWidth - width - 30; // Position X (à droite avec marge de 10)
        int y = screenHeight - height - 10; // Position Y (en bas avec marge de 10)

        // Draw image
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        // Desactivate blend
        GlStateManager.disableBlend();
    }
}