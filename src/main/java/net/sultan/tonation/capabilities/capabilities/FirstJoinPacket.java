package net.sultan.tonation.capabilities.capabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class FirstJoinPacket implements IMessage {
    public int first_join;

    public FirstJoinPacket(int fj)
    {
        this.first_join = fj;
    }

    public FirstJoinPacket()
    {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        first_join = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(first_join);
    }

    public static class ServerHandler implements IMessageHandler<FirstJoinPacket, IMessage> {

        @Override
        public IMessage onMessage(FirstJoinPacket message, MessageContext ctx)
        {
            System.out.println("Packet recu serveur, firstjoin = " + message.first_join);
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(new ScheduledPacketTask(ctx.getServerHandler().player, message));
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler <FirstJoinPacket, IMessage>{

        @Override
        public IMessage onMessage(FirstJoinPacket message, MessageContext ctx)
        {
            System.out.println("Packet recu client, firstjoin = " + message.first_join);
            Minecraft.getMinecraft().addScheduledTask(new ScheduledPacketTask(null, message));
            return null;
        }
    }
}
