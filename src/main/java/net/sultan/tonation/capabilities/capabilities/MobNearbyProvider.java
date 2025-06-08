package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class MobNearbyProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound> {

    protected IMobNearby mobNearby;
    protected EntityPlayer player;

    @Override
    public NBTTagCompound serializeNBT() {
        return (NBTTagCompound) MobNearbyStorage.MOB_NEARBY_CAP.getStorage().writeNBT(MobNearbyStorage.MOB_NEARBY_CAP, mobNearby, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        MobNearbyStorage.MOB_NEARBY_CAP.getStorage().readNBT(MobNearbyStorage.MOB_NEARBY_CAP, mobNearby, null, nbt);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return MobNearbyStorage.MOB_NEARBY_CAP != null && capability == MobNearbyStorage.MOB_NEARBY_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        return this.hasCapability(capability, facing) ? MobNearbyStorage.MOB_NEARBY_CAP.cast(this.mobNearby) : null;
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IMobNearby.class, new MobNearbyStorage(), MobNearby::new);
    }

    public MobNearbyProvider(EntityPlayer player)
    {
        this.mobNearby  = new MobNearby(50);
        this.player = player;
    }
}

