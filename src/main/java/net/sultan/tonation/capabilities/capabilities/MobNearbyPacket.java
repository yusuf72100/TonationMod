package net.sultan.tonation.capabilities.capabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MobNearbyPacket implements IMessage {
    private int timer;

    public MobNearbyPacket() {

    }

    public MobNearbyPacket(int timer) {
        this.timer = timer;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        timer = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(timer);
    }

    // Server Side
    public static class ServerHandler implements IMessageHandler<MobNearbyPacket, IMessage> {

        @Override
        public IMessage onMessage(MobNearbyPacket message, MessageContext ctx) {
            ctx.getServerHandler().player.getServerWorld().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                IMobNearby cap = player.getCapability(MobNearbyStorage.MOB_NEARBY_CAP, null);
                if (cap != null) {
                    cap.setTimer(message.timer);
                }
            });
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public static class ClientHandler implements IMessageHandler <MobNearbyPacket, IMessage>{

        @Override
        public IMessage onMessage(MobNearbyPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().player != null) {
                    Minecraft.getMinecraft().player.getCapability(MobNearbyStorage.MOB_NEARBY_CAP, null)
                            .setTimer(message.timer);
                }
            });
            return null;
        }
    }
}

