package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

public class WarpRain extends IWarpEvent {

    @Override
    public String getName() {
        return "rain";
    }

    @Override
    public int getSeverity() {
        return 12;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        if (!world.getWorldInfo()
            .isThundering()) {
            WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.rain"));
            world.getWorldInfo()
                .setRaining(true);
            world.getWorldInfo()
                .setThundering(true);
            return true;
        }
        return false;
    }
}
