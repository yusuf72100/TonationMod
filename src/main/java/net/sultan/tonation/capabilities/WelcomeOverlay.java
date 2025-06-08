package net.sultan.tonation.capabilities;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class WelcomeOverlay extends GuiScreen {
    private final ResourceLocation background = new ResourceLocation(tonation.MODID, "textures/guibvn.png");
    private final int xSize = 400;
    private final int ySize = 250;
    private int guiLeft;
    private int guiTop;

    @Override
    public void initGui() {
        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        initGui();
        // Afficher le fond par défaut
        drawDefaultBackground();
        mc.getTextureManager().bindTexture(background);
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, xSize, ySize);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}