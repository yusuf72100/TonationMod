package net.sultan.tonation.capabilities.capabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.sultan.tonation.capabilities.WelcomeOverlay;

import java.util.Objects;

public class WelcomeOverlayPacket implements IMessage {

    private String text;

    public WelcomeOverlayPacket() { }

    public WelcomeOverlayPacket(String text) {
        this.text = text;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        text = ByteBufUtils.readUTF8String(buf); // this class is very useful in general for writing more complex objects
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, text);
    }

    public static class ServerHandler implements IMessageHandler<WelcomeOverlayPacket, IMessage> {

        @Override
        public IMessage onMessage(WelcomeOverlayPacket message, MessageContext ctx)
        {
            System.out.println("Packet recu serveur, opengui = " + message.text);
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler <WelcomeOverlayPacket, IMessage>{

        @Override
        public IMessage onMessage(WelcomeOverlayPacket message, MessageContext ctx)
        {
            if(Objects.equals(message.text, "welcome_gui")){
                System.out.println("Packet recu client, opengui = " + message.text);
                IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
                thread.addScheduledTask(() -> {
                    Minecraft.getMinecraft().displayGuiScreen(new WelcomeOverlay());
                });
            }
            return null;
        }
    }
}
