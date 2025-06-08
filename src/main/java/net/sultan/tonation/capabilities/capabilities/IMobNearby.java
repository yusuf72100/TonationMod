package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.entity.player.EntityPlayer;

public interface IMobNearby {
    public int getTimer();
    public void setTimer(int value);
    public void sync(EntityPlayer player);
}