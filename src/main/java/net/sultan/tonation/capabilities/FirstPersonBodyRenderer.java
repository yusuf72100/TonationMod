package net.sultan.tonation.capabilities;

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
import net.minecraftforge.client.event.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.List;

@Mod.EventBusSubscriber(Side.CLIENT)
@SideOnly(Side.CLIENT)
public class FirstPersonBodyRenderer {

    // Cache pour éviter la reflection répétée
    private static Field layersField;
    private static boolean fieldInitialized = false;

    // État pour éviter les calculs répétés
    private static boolean wasFirstPerson = false;
    private static boolean wasInventoryOpen = false;

    // Compteur pour limiter les vérifications coûteuses
    private static int stackTraceCheckCounter = 0;
    private static final int STACK_TRACE_CHECK_INTERVAL = 10; // Vérifier seulement 1 fois sur 10

    static {
        initializeReflection();
    }

    private static void initializeReflection() {
        try {
            layersField = RenderLivingBase.class.getDeclaredField("field_177097_h");
            layersField.setAccessible(true);
            fieldInitialized = true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la reflection: " + e.getMessage());
            fieldInitialized = false;
        }
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (event.getEntity() != mc.player) return;

        boolean isFirstPerson = mc.gameSettings.thirdPersonView == 0;

        // Optimisation: ne vérifier la stack trace que périodiquement
        boolean isInventoryRender = false;
        if (stackTraceCheckCounter++ % STACK_TRACE_CHECK_INTERVAL == 0) {
            isInventoryRender = isInventoryRendering();
        } else {
            isInventoryRender = wasInventoryOpen; // Utiliser la dernière valeur connue
        }

        // Éviter les changements d'état inutiles
        if (isFirstPerson == wasFirstPerson && isInventoryRender == wasInventoryOpen) {
            return;
        }

        boolean shouldHideHelmet = isFirstPerson && !isInventoryRender;
        setHeadVisibility(event.getRenderer(), !shouldHideHelmet);

        // Mettre à jour l'état
        wasFirstPerson = isFirstPerson;
        wasInventoryOpen = isInventoryRender;
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getEntity() == Minecraft.getMinecraft().player) {
            // Restaurer uniquement si nécessaire
            if (!wasFirstPerson || wasInventoryOpen) {
                setHeadVisibility(event.getRenderer(), true);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && mc.gameSettings.thirdPersonView == 0) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        if (player == null || mc.gameSettings.thirdPersonView != 0) {
            return;
        }

        renderFirstPersonBody(player, event.getPartialTicks());
    }

    // Méthode optimisée pour vérifier le rendu d'inventaire
    private static boolean isInventoryRendering() {
        try {
            // Optimisation: limiter la profondeur de recherche
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int maxDepth = Math.min(stackTrace.length, 15); // Limiter à 15 éléments

            for (int i = 0; i < maxDepth; i++) {
                StackTraceElement element = stackTrace[i];
                String className = element.getClassName();
                String methodName = element.getMethodName();

                if (className.contains("GuiInventory") ||
                        methodName.contains("drawEntityOnScreen") ||
                        className.contains("GuiContainer")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    // Méthode unifiée pour gérer la visibilité de la tête
    private static void setHeadVisibility(RenderLivingBase<?> renderer, boolean visible) {
        // Gérer le modèle principal
        if (renderer.getMainModel() instanceof ModelBiped) {
            ModelBiped model = (ModelBiped) renderer.getMainModel();
            model.bipedHead.isHidden = !visible;
            model.bipedHeadwear.isHidden = !visible;
        }

        // Gérer les couches d'armure
        setArmorHeadVisibility(renderer, visible);
    }

    // Méthode optimisée pour gérer l'armure
    private static void setArmorHeadVisibility(RenderLivingBase<?> renderer, boolean visible) {
        if (!fieldInitialized) return;

        try {
            @SuppressWarnings("unchecked")
            List<LayerRenderer<?>> layers = (List<LayerRenderer<?>>) layersField.get(renderer);

            if (layers != null) {
                for (LayerRenderer<?> layer : layers) {
                    if (layer instanceof LayerArmorBase) {
                        LayerArmorBase<?> armorLayer = (LayerArmorBase<?>) layer;
                        ModelBiped armorModel = (ModelBiped) armorLayer.getModelFromSlot(EntityEquipmentSlot.HEAD);
                        if (armorModel != null) {
                            armorModel.bipedHead.isHidden = !visible;
                            armorModel.bipedHeadwear.isHidden = !visible;
                            Minecraft.getMinecraft().player.sendChatMessage("isHidden: " + !visible);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // nothing
        }
    }

    // Méthode optimisée pour le rendu du corps
    private static void renderFirstPersonBody(EntityPlayerSP player, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        try {
            // Configuration pour un rendu opaque correct
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            // Calcul de position optimisé
            double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

            GlStateManager.translate(
                    px - mc.getRenderManager().viewerPosX,
                    py - mc.getRenderManager().viewerPosY,
                    pz - mc.getRenderManager().viewerPosZ
            );

            Render<?> render = mc.getRenderManager().getEntityRenderObject(player);
            if (render instanceof RenderPlayer) {
                RenderPlayer renderPlayer = (RenderPlayer) render;

                // Sauvegarder l'état original
                boolean oldHeadHidden = renderPlayer.getMainModel().bipedHead.isHidden;
                boolean oldHeadwearHidden = renderPlayer.getMainModel().bipedHeadwear.isHidden;

                // Cacher la tête temporairement
                renderPlayer.getMainModel().bipedHead.isHidden = true;
                renderPlayer.getMainModel().bipedHeadwear.isHidden = true;
                setArmorHeadVisibility(renderPlayer, false);

                // Rendu du corps avec les bons paramètres d'opacité
                renderPlayer.doRender(player, 0, 0, 0, player.rotationYaw, partialTicks);

                // Restaurer l'état original
                renderPlayer.getMainModel().bipedHead.isHidden = oldHeadHidden;
                renderPlayer.getMainModel().bipedHeadwear.isHidden = oldHeadwearHidden;
                setArmorHeadVisibility(renderPlayer, !oldHeadwearHidden);
            }
        } catch (OutOfMemoryError e) {
            // Log les erreurs critiques
            e.printStackTrace();
        } catch (Exception e) {
            // Ignorer les autres exceptions pour éviter le spam de logs
        } finally {
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
            GlStateManager.translate(0, -0.3f, 0);
        }
    }
}