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
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(Side.CLIENT)
public class CustomHealthOverlay {

    // Variables pour l'animation
    private static float targetHealthPercentage = 1.0f;
    private static float currentAnimatedHealth = 1.0f;
    private static long lastUpdateTime = System.currentTimeMillis();

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            renderCustomHealth();
        }
    }

    private static void renderCustomHealth() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc.player;

        if (player == null) return;

        // Screen dimensions
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        int screenWidth = scaledResolution.getScaledWidth();
        int screenHeight = scaledResolution.getScaledHeight();

        // Position
        int width = 16;
        int height = 16;
        int x = screenWidth - width - 10;
        int y = screenHeight - height - 10;

        // Calcule le pourcentage de vie cible
        float currentHealth = player.getHealth();
        float maxHealth = player.getMaxHealth();
        targetHealthPercentage = currentHealth / maxHealth;

        // Animation smooth
        updateHealthAnimation();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // 1. Image de fond
        ResourceLocation backgroundTexture = new ResourceLocation(tonation.MODID, "textures/health.png");
        mc.getTextureManager().bindTexture(backgroundTexture);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        // 2. Image qui se remplit avec l'animation
        ResourceLocation fillTexture = new ResourceLocation(tonation.MODID, "textures/health_fill.png");
        mc.getTextureManager().bindTexture(fillTexture);

        // Utilise la valeur animée au lieu de la valeur réelle
        int visibleHeight = (int)(height * currentAnimatedHealth);
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

        // 3. Image principale
        ResourceLocation mainTexture = new ResourceLocation(tonation.MODID, "textures/health.png");
        mc.getTextureManager().bindTexture(mainTexture);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);

        GlStateManager.disableBlend();
    }

    private static void updateHealthAnimation() {
        long currentTime = System.currentTimeMillis();
        float deltaTime = (currentTime - lastUpdateTime) / 1000.0f; // Delta en secondes
        lastUpdateTime = currentTime;

        // Vitesse d'animation (ajuste selon tes préférences)
        float animationSpeed = 2.0f; // Plus élevé = plus rapide

        // Calcule la différence
        float difference = targetHealthPercentage - currentAnimatedHealth;

        // Si la différence est très petite, snap à la valeur cible
        if (Math.abs(difference) < 0.01f) {
            currentAnimatedHealth = targetHealthPercentage;
        } else {
            // Interpolation smooth
            currentAnimatedHealth += difference * animationSpeed * deltaTime;
        }

        // Clamp entre 0 et 1
        currentAnimatedHealth = Math.max(0.0f, Math.min(1.0f, currentAnimatedHealth));
    }
}
