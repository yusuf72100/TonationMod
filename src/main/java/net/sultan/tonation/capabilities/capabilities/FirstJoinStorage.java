package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class FirstJoinStorage implements Capability.IStorage<IFirstJoin> {

    @CapabilityInject(IFirstJoin.class)
    public static final Capability <IFirstJoin>FIRST_JOIN_CAP = null;

    @Override
    public NBTBase writeNBT(Capability<IFirstJoin> capability, IFirstJoin instance, EnumFacing side)
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("first_join", instance.isFirstConnection());
        return nbt;
    }

    @Override
    public void readNBT(Capability<IFirstJoin> capability, IFirstJoin instance, EnumFacing side, NBTBase base)
    {
        if(base instanceof NBTTagCompound) {
            NBTTagCompound nbt = (NBTTagCompound)base;
            instance.setFirstJoin();
        }
    }
}
