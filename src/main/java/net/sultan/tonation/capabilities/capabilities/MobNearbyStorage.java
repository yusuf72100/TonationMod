package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class MobNearbyStorage implements Capability.IStorage<IMobNearby> {

    @CapabilityInject(IMobNearby.class)
    public static final Capability <IMobNearby>MOB_NEARBY_CAP = null;

    @Override
    public NBTBase writeNBT(Capability<IMobNearby> capability, IMobNearby instance, EnumFacing side) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("timer", instance.getTimer());
        return nbt;
    }

    @Override
    public void readNBT(Capability<IMobNearby> capability, IMobNearby instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            instance.setTimer(compound.getInteger("timer"));
        }
    }
}
