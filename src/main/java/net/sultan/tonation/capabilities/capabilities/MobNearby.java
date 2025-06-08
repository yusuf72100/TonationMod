package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.sultan.tonation.capabilities.tonation;

public class MobNearby implements IMobNearby {
    private int timer;

    public MobNearby(int timer) {
        this.timer = timer;
    }

    public MobNearby() {

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
            EntityPlayerMP playerMP = (EntityPlayerMP)player;
            tonation.network.sendTo(packet, playerMP);
        }
        else
        {
            tonation.network.sendToServer(packet);
        }
    }
}
