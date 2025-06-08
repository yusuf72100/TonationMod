package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.sultan.tonation.capabilities.tonation;

public class MobNearby implements IMobNearby {
    private int timer;

    public MobNearby() {
        this.timer = 0;
    }

    @Override
    public int getTimer() {
        return timer;
    }

    @Override
    public void setTimer(int value) {
        this.timer = value;
    }

    public void sync(EntityPlayer player)       //sync data between player and server
    {
        MobNearbyPacket packet = new MobNearbyPacket(this.timer);

        if(!player.world.isRemote)
        {
            System.out.println("sync, isRemote ? : no, send to player");
            EntityPlayerMP playerMP = (EntityPlayerMP)player;
            tonation.network.sendTo(packet, playerMP);
        }
        else
        {
            System.out.println("sync, isRemote ? : yes, send to server");
            tonation.network.sendToServer(packet);
        }
    }
}
