package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.sultan.tonation.capabilities.tonation;

public class FirstJoin implements IFirstJoin{
    public int first_join;

    public FirstJoin()
    {

    }

    public FirstJoin(int i)
    {
        this.first_join = i;
    }

    @Override
    public void setFirstJoin()
    {
        this.first_join = 0;
    }

    @Override
    public int isFirstConnection()
    {
        return this.first_join;
    }

    @Override
    public void sync(EntityPlayer player) //sync data between player and server
    {
        FirstJoinPacket packet = new FirstJoinPacket(first_join);

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
