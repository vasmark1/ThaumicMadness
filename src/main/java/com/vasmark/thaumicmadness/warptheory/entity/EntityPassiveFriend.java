package com.vasmark.thaumicmadness.warptheory.entity;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.world.World;

public class EntityPassiveFriend extends EntityCreeper {

    public EntityPassiveFriend(World world) {
        super(world);
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth)
            .setBaseValue(100.0D);
        this.setHealth(100.0F);
    }

    @Override
    public int getCreeperState() {
        return -1; // Never explode
    }

    @Override
    public boolean allowLeashing() {
        return true;
    }
}
