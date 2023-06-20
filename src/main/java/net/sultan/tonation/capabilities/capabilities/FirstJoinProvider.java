package net.sultan.tonation.capabilities.capabilities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class FirstJoinProvider implements ICapabilityProvider, INBTSerializable<NBTTagCompound>
{
        protected IFirstJoin firstJoin;
        protected EntityPlayer player;

        @Override
        public NBTTagCompound serializeNBT() {
                return (NBTTagCompound) FirstJoinStorage.FIRST_JOIN_CAP.writeNBT(this.firstJoin, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
                FirstJoinStorage.FIRST_JOIN_CAP.readNBT(this.firstJoin, null, nbt);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
                return FirstJoinStorage.FIRST_JOIN_CAP != null && capability == FirstJoinStorage.FIRST_JOIN_CAP;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
                return this.hasCapability(capability, facing) ? FirstJoinStorage.FIRST_JOIN_CAP.cast(this.firstJoin) : null;
        }

        public static void register()
        {
                CapabilityManager.INSTANCE.register(IFirstJoin.class, new FirstJoinStorage(), FirstJoin::new);
        }

        public FirstJoinProvider(EntityPlayer player)
        {
                this.firstJoin  = new FirstJoin(1);
                this.player = player;
        }

        public IFirstJoin getFirstJoinObj()
        {
                return firstJoin;
        }
}