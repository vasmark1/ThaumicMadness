package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;

import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WarpTongue extends IWarpEvent {

    public WarpTongue() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public String getName() {
        return "tongue";
    }

    @Override
    public int getSeverity() {
        return 11;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        WarpTheoryHelper.sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory.tongue"));
        WarpTheoryHelper.modEventInt(player, "tongues", 10 + world.rand.nextInt(15));
        return true;
    }

    @SubscribeEvent
    public void onMessageReceived(ServerChatEvent e) {
        NBTTagCompound tag = WarpTheoryHelper.getWarpTag(e.player);
        if (tag.hasKey("tongues")) {
            int tongues = tag.getInteger("tongues");
            String original = e.message;
            StringBuilder garbled = new StringBuilder();
            for (char c : original.toCharArray()) {
                if (Character.isLetter(c) && e.player.worldObj.rand.nextBoolean()) {
                    garbled.append("§k")
                        .append(c)
                        .append("§r");
                } else {
                    garbled.append(c);
                }
            }
            e.component = new ChatComponentTranslation("<" + e.player.getDisplayName() + "> " + garbled.toString());
            --tongues;
            tag.setInteger("tongues", tongues);
            if (tongues <= 0) {
                tag.removeTag("tongues");
            }
        }
    }
}
