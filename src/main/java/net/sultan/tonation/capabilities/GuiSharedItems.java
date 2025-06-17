package net.sultan.tonation.capabilities;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sultan.tonation.capabilities.capabilities.RequestSharedItemPacket;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiSharedItems extends GuiScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("tonation", "textures/gui/shared_items.png");
    private final EntityPlayer targetPlayer;
    private List<SharedItemManager.SharedItem> sharedItems;

    public GuiSharedItems(EntityPlayer targetPlayer) {
        this.targetPlayer = targetPlayer;
        this.sharedItems = SharedItemManager.getSharedItems(targetPlayer.getUniqueID());
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        int guiLeft = (width - 176) / 2;
        int guiTop = (height - 166) / 2;

        // Dessiner le fond (ou un rectangle simple si pas de texture)
        drawRect(guiLeft, guiTop, guiLeft + 176, guiTop + 166, 0xC0101010);
        drawRect(guiLeft + 1, guiTop + 1, guiLeft + 175, guiTop + 165, 0xFF8B8B8B);

        // Dessiner le titre
        String title = "Items partagés par " + targetPlayer.getName();
        fontRenderer.drawString(title, guiLeft + 8, guiTop + 6, 0x404040);

        // Dessiner les items
        for (int i = 0; i < Math.min(sharedItems.size(), 36); i++) {
            int x = guiLeft + 8 + (i % 9) * 18;
            int y = guiTop + 18 + (i / 9) * 18;

            SharedItemManager.SharedItem sharedItem = sharedItems.get(i);

            // Dessiner l'item
            RenderHelper.enableGUIStandardItemLighting();
            itemRender.renderItemAndEffectIntoGUI(sharedItem.item, x, y);
            itemRender.renderItemOverlayIntoGUI(fontRenderer, sharedItem.item, x, y, null);
            RenderHelper.disableStandardItemLighting();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton == 1) { // Clic droit
            int guiLeft = (width - 176) / 2;
            int guiTop = (height - 166) / 2;

            for (int i = 0; i < Math.min(sharedItems.size(), 36); i++) {
                int x = guiLeft + 8 + (i % 9) * 18;
                int y = guiTop + 18 + (i / 9) * 18;

                if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                    // Demander l'item au serveur
                    tonation.network.sendToServer(new RequestSharedItemPacket(targetPlayer.getUniqueID(), i));
                    break;
                }
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
