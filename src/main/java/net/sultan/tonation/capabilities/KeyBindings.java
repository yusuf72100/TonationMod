package net.sultan.tonation.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sultan.tonation.capabilities.capabilities.ShareItemPacket;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeyBindings {
    public static final KeyBinding SHARE_ITEM = new KeyBinding("key.tonation.shareitem", Keyboard.KEY_G, "key.categories.tonation");

    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (SHARE_ITEM.isPressed()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.player != null && !mc.player.getHeldItemMainhand().isEmpty()) {
                // Envoyer un packet au serveur
                tonation.network.sendToServer(new ShareItemPacket());
            }
        }
    }
}
