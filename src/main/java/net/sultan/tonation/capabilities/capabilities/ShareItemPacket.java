package net.sultan.tonation.capabilities.capabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.sultan.tonation.capabilities.SharedItemManager;
import net.sultan.tonation.capabilities.tonation;

public class ShareItemPacket implements IMessage {

    public ShareItemPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    public static class Handler implements IMessageHandler<ShareItemPacket, IMessage> {
        @Override
        public IMessage onMessage(ShareItemPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                ItemStack heldItem = player.getHeldItemMainhand().copy();
                if (!heldItem.isEmpty()) {
                    // Déclencher l'animation
                    tonation.network.sendToAllAround(
                            new TriggerAnimationPacket(player.getEntityId(), heldItem),
                            new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 32)
                    );


                    // Ajouter l'item à la liste des items partagés
                    SharedItemManager.addSharedItem(player, heldItem);
                } 
            });
            return null;
        }
    }
}
