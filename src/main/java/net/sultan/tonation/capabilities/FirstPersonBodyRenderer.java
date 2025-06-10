package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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

    private static Field layersField;
    private static boolean fieldInitialized = false;

    private static boolean wasFirstPerson = false;
    private static boolean wasInventoryOpen = false;

    private static int stackTraceCheckCounter = 0;
    private static final int STACK_TRACE_CHECK_INTERVAL = 10;

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

        boolean isInventoryRender = false;
        if (stackTraceCheckCounter++ % STACK_TRACE_CHECK_INTERVAL == 0) {
            isInventoryRender = isInventoryRendering();
        } else {
            isInventoryRender = wasInventoryOpen;
        }

        if (isFirstPerson == wasFirstPerson && isInventoryRender == wasInventoryOpen) {
            return;
        }

        boolean shouldHideHelmet = isFirstPerson && !isInventoryRender;
        setHeadVisibility(event.getRenderer(), !shouldHideHelmet);

        wasFirstPerson = isFirstPerson;
        wasInventoryOpen = isInventoryRender;
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getEntity() == Minecraft.getMinecraft().player) {
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

    private static boolean isInventoryRendering() {
        try {
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int maxDepth = Math.min(stackTrace.length, 15);

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

    private static void setHeadVisibility(RenderLivingBase<?> renderer, boolean visible) {
        if (renderer.getMainModel() instanceof ModelBiped) {
            ModelBiped model = (ModelBiped) renderer.getMainModel();
            model.bipedHead.isHidden = !visible;
            model.bipedHeadwear.isHidden = !visible;
        }
        setArmorHeadVisibility(renderer, visible);
    }

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
                        }
                    }
                }
            }
        } catch (Exception e) {
            // silent fail
        }
    }

    private static void renderFirstPersonBody(EntityPlayerSP player, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        try {
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.disableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            // **Activation de la lumière pour shaders**
            RenderHelper.enableStandardItemLighting();
            GlStateManager.enableLighting();
            GlStateManager.enableRescaleNormal();

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

                boolean oldHeadHidden = renderPlayer.getMainModel().bipedHead.isHidden;
                boolean oldHeadwearHidden = renderPlayer.getMainModel().bipedHeadwear.isHidden;

                renderPlayer.getMainModel().bipedHead.isHidden = true;
                renderPlayer.getMainModel().bipedHeadwear.isHidden = true;
                setArmorHeadVisibility(renderPlayer, false);

                renderPlayer.doRender(player, 0, 0, 0, player.rotationYaw, partialTicks);

                renderPlayer.getMainModel().bipedHead.isHidden = oldHeadHidden;
                renderPlayer.getMainModel().bipedHeadwear.isHidden = oldHeadwearHidden;
                setArmorHeadVisibility(renderPlayer, !oldHeadwearHidden);
            }

            // **Désactivation lumière pour ne pas polluer la suite**
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableLighting();

        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception e) {
            // silence exceptions
        } finally {
            GlStateManager.popMatrix();
            GlStateManager.popAttrib();
        }
    }

    @SubscribeEvent
    public static void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.thirdPersonView == 0) {
            if (mc.player != null && mc.player.isSneaking()) {
                GlStateManager.translate(0, -0.4f, 0.1f);
            } else {
                GlStateManager.translate(0, -0.3f, 0);
            }
        }
    }
}
