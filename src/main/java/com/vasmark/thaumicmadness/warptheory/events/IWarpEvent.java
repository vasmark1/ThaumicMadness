package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class IWarpEvent {

    public abstract String getName();

    public abstract int getSeverity();

    public final int getCost() {
        return (int) Math.ceil(this.getSeverity() / 10.0D);
    }

    public boolean canDo(EntityPlayer player) {
        return true;
    }

    public abstract boolean doEvent(World world, EntityPlayer player);
}
