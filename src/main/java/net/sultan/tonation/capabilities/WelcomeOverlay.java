package net.sultan.tonation.capabilities;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class WelcomeOverlay extends GuiScreen {
    private final ResourceLocation background = new ResourceLocation(tonation.MODID, "textures/guibvn.png");
    private final int xSize = 400;
    private final int ySize = 250;
    private int guiLeft;
    private int guiTop;

    public void initGui() {
        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // Afficher le fond par défaut
        //drawBackgroundImage();
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(background);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, xSize, ySize);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void drawBackgroundImage() {
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        GlStateManager.popMatrix();
    }
}