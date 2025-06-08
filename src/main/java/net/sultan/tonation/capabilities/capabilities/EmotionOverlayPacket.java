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
import net.sultan.tonation.capabilities.EmotionalOverlay;
import net.sultan.tonation.capabilities.WelcomeOverlay;

import java.util.Objects;

public class EmotionOverlayPacket implements IMessage {

    private String text;

    public EmotionOverlayPacket() { }

    public EmotionOverlayPacket(String text) {
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

    public static class ServerHandler implements IMessageHandler<EmotionOverlayPacket, IMessage> {

        @Override
        public IMessage onMessage(EmotionOverlayPacket message, MessageContext ctx)
        {
            System.out.println("Packet recu serveur, emotional = " + message.text);
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler <EmotionOverlayPacket, IMessage>{

        @Override
        public IMessage onMessage(EmotionOverlayPacket message, MessageContext ctx)
        {
            System.out.println("Packet recu client, emotional = " + message.text);
            IThreadListener thread = FMLCommonHandler.instance().getWorldThread(ctx.netHandler);
            thread.addScheduledTask(() -> {
                EmotionalOverlay.setEmotion(message.text);
            });
            return null;
        }
    }
}
