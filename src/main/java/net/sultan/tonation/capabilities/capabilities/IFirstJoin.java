package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.entity.player.EntityPlayer;

public interface IFirstJoin {
    public void setFirstJoin();
    public int isFirstConnection();
    public void sync(EntityPlayer player);
}
