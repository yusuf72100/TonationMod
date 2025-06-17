package net.sultan.tonation.capabilities.capabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.sultan.tonation.capabilities.AnimationHandler;
import net.sultan.tonation.capabilities.tonation;

public class TriggerAnimationPacket implements IMessage{
    private int entityId;
    private ItemStack itemStack;

    public TriggerAnimationPacket() {}

    public TriggerAnimationPacket(int entityId, ItemStack itemStack) {
        this.entityId = entityId;
        this.itemStack = itemStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        itemStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        ByteBufUtils.writeItemStack(buf, itemStack);
    }

    public static class Handler implements IMessageHandler<TriggerAnimationPacket, IMessage> {
        @Override
        public IMessage onMessage(TriggerAnimationPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    // Déclencher l'animation personnalisée
                    AnimationHandler.triggerShareAnimation(player, message.itemStack);
                    Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Reçu"));
                }
            });
            return null;
        }
    }

}
