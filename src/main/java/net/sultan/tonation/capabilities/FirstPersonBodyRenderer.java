package net.sultan.tonation.capabilities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public class FirstPersonBodyRenderer {

    private static boolean shouldHideHelmet = false;

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (event.getEntity() != mc.player) return;

        boolean isFirstPerson = mc.gameSettings.thirdPersonView == 0;

        // Vérifie si on est dans le rendu de l'inventaire en analysant la stack trace
        boolean isInventoryRender = false;
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stackTrace) {
                if (element.getClassName().contains("GuiInventory") ||
                        element.getMethodName().contains("drawEntityOnScreen")) {
                    isInventoryRender = true;
                    break;
                }
            }
        } catch (Exception e) {
            isInventoryRender = false;
        }

        // Définit si on doit cacher le casque (1re personne et pas inventaire)
        shouldHideHelmet = isFirstPerson && !isInventoryRender;

        if (shouldHideHelmet) {
            // Cache la tête et le casque uniquement en 1re personne hors inventaire
            event.getRenderer().getMainModel().bipedHead.isHidden = true;
            event.getRenderer().getMainModel().bipedHeadwear.isHidden = true;
            hideHelmetFromArmorLayer(event.getRenderer(), true);

        } else {
            // Restaure la tête et le casque visibles dans tous les autres cas (3e personne et inventaire)
            event.getRenderer().getMainModel().bipedHead.isHidden = false;
            event.getRenderer().getMainModel().bipedHeadwear.isHidden = false;
            hideHelmetFromArmorLayer(event.getRenderer(), false);
        }

        // *** SUPPRESSION de la boucle cachant encore le casque ici ***
        // Car déjà géré plus haut, et ça causait le problème en 3e personne/inventaire
    }

    private static void hideHelmetFromArmorLayer(RenderLivingBase<?> renderer, boolean hide) {
        List<LayerRenderer<?>> layers = getLayers(renderer);
        if (layers == null) return;

        for (LayerRenderer<?> layer : layers) {
            if (layer instanceof LayerArmorBase) {
                LayerArmorBase<?> armorLayer = (LayerArmorBase<?>) layer;
                ModelBiped model = (ModelBiped) armorLayer.getModelFromSlot(EntityEquipmentSlot.HEAD);
                if (model != null) {
                    // Utilise showModel pour gérer la visibilité
                    model.bipedHead.isHidden = hide;
                    model.bipedHeadwear.isHidden = hide;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        // Restaure la tête et casque visibles dans tous les cas
        if (event.getEntity() == Minecraft.getMinecraft().player) {
            event.getRenderer().getMainModel().bipedHead.isHidden = false;
            event.getRenderer().getMainModel().bipedHeadwear.isHidden = false;
            hideHelmetFromArmorLayer(event.getRenderer(), false);
            shouldHideHelmet = false;
        }
    }

    // Event pour cacher spécifiquement les casques en première personne (hors inventaire)
    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();

        if (event.getEntity() == mc.player && mc.gameSettings.thirdPersonView == 0) {

            boolean isInventoryRender = false;
            try {
                for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
                    if (element.getClassName().contains("GuiInventory") ||
                            element.getMethodName().contains("drawEntityOnScreen")) {
                        isInventoryRender = true;
                        break;
                    }
                }
            } catch (Exception ignored) {}

            if (!isInventoryRender) {
                shouldHideHelmet = true;
                hideHelmetFromArmorLayer(event.getRenderer(), true);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static List<LayerRenderer<?>> getLayers(RenderLivingBase<?> renderer) {
        try {
            Field layerField = RenderLivingBase.class.getDeclaredField("layerRenderers");
            layerField.setAccessible(true);
            return (List<LayerRenderer<?>>) layerField.get(renderer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (event.getEntity() == Minecraft.getMinecraft().player) {
            shouldHideHelmet = false;
            hideHelmetFromArmorLayer(event.getRenderer(), false);
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        if (player == null) return;

        // Cache toujours les mains en première personne
        if (mc.gameSettings.thirdPersonView == 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        if (player == null || mc.gameSettings.thirdPersonView != 0)
            return;

        renderFirstPersonBody(player, event.getPartialTicks());
    }

    private static void renderFirstPersonBody(EntityPlayerSP player, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushAttrib();
        GlStateManager.pushMatrix();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
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

                // Cache la tête pour le rendu du corps en 1re personne
                renderPlayer.getMainModel().bipedHead.isHidden = true;
                renderPlayer.getMainModel().bipedHeadwear.isHidden = true;
                hideHelmetFromArmorLayer(renderPlayer, true);

                renderPlayer.doRender(player, 0, 0, 0, player.rotationYaw, partialTicks);

                // Restaure les valeurs originales
                renderPlayer.getMainModel().bipedHead.showModel = oldHead;
                renderPlayer.getMainModel().bipedHeadwear.showModel = oldHeadwear;
                renderPlayer.getMainModel().bipedHead.isHidden = false;
                renderPlayer.getMainModel().bipedHeadwear.isHidden = false;
                hideHelmetFromArmorLayer(renderPlayer, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    @SubscribeEvent
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.thirdPersonView == 0) {
            // Pousse la caméra vers le haut pour simuler la vue depuis les yeux
            GlStateManager.translate(0, -0.3f, 0);
        }
    }
}
