package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void Init() {
        super.Init();
        MinecraftForge.EVENT_BUS.register(EmotionalOverlay.class);
        MinecraftForge.EVENT_BUS.register(KeyBindings.class);
        MinecraftForge.EVENT_BUS.register(AnimationHandler.class);
        MinecraftForge.EVENT_BUS.register(new PlayerAnimationRenderer());

        ClientRegistry.registerKeyBinding(KeyBindings.SHARE_ITEM);
    }
}
