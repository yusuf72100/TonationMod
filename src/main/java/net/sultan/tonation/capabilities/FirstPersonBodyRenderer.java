package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public class FirstPersonBodyRenderer {

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (event.getEntity() != mc.player) return;

        boolean isRenderingInWorld = mc.currentScreen == null && mc.gameSettings.thirdPersonView == 0;

        if (isRenderingInWorld) {
            event.getRenderer().getMainModel().bipedHead.isHidden = true;
            event.getRenderer().getMainModel().bipedHeadwear.isHidden = true;
        } else {
            event.getRenderer().getMainModel().bipedHead.isHidden = false;
            event.getRenderer().getMainModel().bipedHeadwear.isHidden = false;
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Pre event) {
        // On restaure la tête dans tous les cas pour éviter les bugs visuels
        if (event.getEntity() == Minecraft.getMinecraft().player) {
            event.getRenderer().getMainModel().bipedHead.isHidden = false;
            event.getRenderer().getMainModel().bipedHeadwear.isHidden = false;
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        if (player == null) return;

        // Vérifie si le joueur est en train d'attaquer/utiliser un objet
        /*boolean isSwinging = player.isSwingInProgress;
        boolean isUsingItem = player.isHandActive();
        boolean isDigging = mc.playerController.getIsHittingBlock();

        // Si le joueur n'est pas en train d'attaquer/utiliser quelque chose, cache le bras
        if (!isSwinging && !isUsingItem && !isDigging) {
            event.setCanceled(true);
        }*/
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        // Vérifie si on est en première personne
        if (player == null || mc.gameSettings.thirdPersonView != 0 || mc.currentScreen != null)
            return;

        renderFirstPersonBody(player, event.getPartialTicks());
    }

    private static void renderFirstPersonBody(EntityPlayerSP player, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);

        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        GlStateManager.translate(
                px - mc.getRenderManager().viewerPosX,
                py - mc.getRenderManager().viewerPosY,
                pz - mc.getRenderManager().viewerPosZ
        );

        try {
            Render<?> render = mc.getRenderManager().getEntityRenderObject(player);
            if (render instanceof RenderPlayer) {
                RenderPlayer renderPlayer = (RenderPlayer) render;

                boolean oldHead = renderPlayer.getMainModel().bipedHead.showModel;
                boolean oldHeadwear = renderPlayer.getMainModel().bipedHeadwear.showModel;

                // Cache la tête juste pour ce rendu
                renderPlayer.getMainModel().bipedHead.isHidden = true;
                renderPlayer.getMainModel().bipedHeadwear.showModel = true;

                renderPlayer.doRender(player, 0, 0, 0, player.rotationYaw, partialTicks);

                // Restaure
                renderPlayer.getMainModel().bipedHead.showModel = oldHead;
                renderPlayer.getMainModel().bipedHeadwear.showModel = oldHeadwear;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @SubscribeEvent
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.thirdPersonView == 0) {
            // push camera up
            GlStateManager.translate(0, -0.3f, 0);
        }
    }
}