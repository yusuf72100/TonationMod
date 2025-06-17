package net.sultan.tonation.capabilities;

import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PlayerAnimationRenderer {

    @SubscribeEvent
    public void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();

        if (!AnimationHandler.isPlayerAnimating(player)) {
            return;
        }

        ModelPlayer model = event.getRenderer().getMainModel();
        float progress = AnimationHandler.getAnimationProgress(player);

        // Sauvegarde des rotations originales si nécessaire
        // (optionnel, dépend de votre implémentation)

        model.bipedRightArm.rotateAngleX = -1.5F * progress;
        model.bipedLeftArm.rotateAngleX = -1.5F * progress;

        model.bipedRightArm.rotateAngleY = 0.2F * progress;
        model.bipedLeftArm.rotateAngleY = -0.2F * progress;

        model.bipedRightArm.rotateAngleZ = -0.1F * progress;
        model.bipedLeftArm.rotateAngleZ = 0.1F * progress;

        tonation.LOGGER.info("Animation progress for " + player.getName() + ": " + progress);
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        EntityPlayer player = event.getEntityPlayer();

        if (!AnimationHandler.isPlayerAnimating(player)) {
            return;
        }

        // Optionnel : restaurer les rotations originales après le rendu
        // pour éviter d'affecter d'autres rendus
        ModelPlayer model = event.getRenderer().getMainModel();

        // Réinitialiser les rotations si nécessaire
        // model.bipedRightArm.rotateAngleX = 0F;
        // model.bipedLeftArm.rotateAngleX = 0F;
        // etc...
    }
}