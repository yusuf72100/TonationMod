package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ScheduledPacketTask implements Runnable
{
    private EntityPlayer player;
    private FirstJoinPacket message;

    public ScheduledPacketTask(EntityPlayer player, FirstJoinPacket message)
    {
        this.player = player;
        this.message = message;
    }

    @Override
    public void run()
    {
        EntityPlayer player = this.player == null ? getPlayer() : this.player;
        player.getCapability(FirstJoinStorage.FIRST_JOIN_CAP, null).setFirstJoin();
    }

    @SideOnly(Side.CLIENT)
    private EntityPlayer getPlayer()
    {
        return Minecraft.getMinecraft().player;
    }
}
