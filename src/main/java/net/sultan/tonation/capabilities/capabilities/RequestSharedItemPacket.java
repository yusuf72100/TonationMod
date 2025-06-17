package net.sultan.tonation.capabilities.capabilities;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.sultan.tonation.capabilities.SharedItemManager;

import java.util.List;
import java.util.UUID;

public class RequestSharedItemPacket implements IMessage {
    private UUID playerId;
    private int itemIndex;

    public RequestSharedItemPacket() {

    }

    public RequestSharedItemPacket(UUID playerId, int itemIndex) {
        this.playerId = playerId;
        this.itemIndex = itemIndex;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerId = new UUID(buf.readLong(), buf.readLong());
        itemIndex = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(playerId.getMostSignificantBits());
        buf.writeLong(playerId.getLeastSignificantBits());
        buf.writeInt(itemIndex);
    }

    public static class Handler implements IMessageHandler<RequestSharedItemPacket, IMessage> {
        @Override
        public IMessage onMessage(RequestSharedItemPacket message, MessageContext ctx) {
            EntityPlayerMP requester = ctx.getServerHandler().player;
            requester.getServerWorld().addScheduledTask(() -> {
                List<SharedItemManager.SharedItem> items = SharedItemManager.getSharedItems(message.playerId);
                if (message.itemIndex >= 0 && message.itemIndex < items.size()) {
                    SharedItemManager.SharedItem sharedItem = items.get(message.itemIndex);

                    // Donner l'item au joueur
                    if (!requester.inventory.addItemStackToInventory(sharedItem.item.copy())) {
                        // Si l'inventaire est plein, drop l'item
                        requester.dropItem(sharedItem.item.copy(), false);
                    }
                }
            });
            return null;
        }
    }
}
