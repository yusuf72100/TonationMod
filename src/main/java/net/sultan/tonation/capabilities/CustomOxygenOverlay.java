package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class CustomOxygenOverlay {

    private static float targetOxygenPercentage = 1.0f;
    private static float currentAnimatedOxygen = 1.0f;
    private static long lastUpdateTime = System.currentTimeMillis();

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            renderCustomOxygen();
        }
    }

    private static void renderCustomOxygen() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        if (player == null) return;

        // Dimensions de l'écran
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();

        int width = 16;
        int height = 16;
        int x = screenWidth - width - 120;
        int y = screenHeight - height - 10;

        // Vanilla max d'air est 300 (valeur standard)
        float currentOxygen = player.getAir();
        float maxOxygen = 300.0f;

        targetOxygenPercentage = currentOxygen / maxOxygen;

        updateOxygenAnimation();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Texture de fond (cadre)
        ResourceLocation backgroundTexture = new ResourceLocation("tonation", "textures/oxygen.png");
        mc.getTextureManager().bindTexture(backgroundTexture);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        // Texture de remplissage (barre d'oxygène)
        ResourceLocation fillTexture = new ResourceLocation("tonation", "textures/oxygen_fill.png");
        mc.getTextureManager().bindTexture(fillTexture);

        int visibleHeight = (int)(height * currentAnimatedOxygen);
        int hiddenHeight = height - visibleHeight;

        if (visibleHeight > 0) {
            float textureY = (float)hiddenHeight / height * 16;

            Gui.drawModalRectWithCustomSizedTexture(
                    x, y + hiddenHeight,
                    0, textureY,
                    width, visibleHeight,
                    16, 16
            );
        }

        // Re-dessine le cadre principal par-dessus pour bien encadrer
        mc.getTextureManager().bindTexture(backgroundTexture);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        GlStateManager.disableBlend();
    }

    private static void updateOxygenAnimation() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f;
        lastUpdateTime = currentTime;

        float animationSpeed = 2.0f;

        float difference = targetOxygenPercentage - currentAnimatedOxygen;

        if (Math.abs(difference) < 0.01f) {
            currentAnimatedOxygen = targetOxygenPercentage;
        } else {
            currentAnimatedOxygen += difference * animationSpeed * deltaTime;
        }

        currentAnimatedOxygen = Math.max(0.0f, Math.min(1.0f, currentAnimatedOxygen));
    }
}
