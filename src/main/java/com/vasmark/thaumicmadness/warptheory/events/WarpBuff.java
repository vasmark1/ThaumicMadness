package com.vasmark.thaumicmadness.warptheory.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.vasmark.thaumicmadness.warptheory.util.WarpTheoryHelper;

public class WarpBuff extends IWarpEvent {

    private final String name;
    private final int severity;
    private final int potionId;
    private final int duration;
    private final int level;

    public WarpBuff(String name, int severity, PotionEffect effect) {
        this.name = name;
        this.severity = severity;
        this.potionId = effect.getPotionID();
        this.duration = effect.getDuration();
        this.level = effect.getAmplifier();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getSeverity() {
        return this.severity;
    }

    @Override
    public boolean doEvent(World world, EntityPlayer player) {
        if (world.isRemote) return true;

        PotionEffect effect = null;
        if (player.isPotionActive(this.potionId)) {
            for (Object obj : player.getActivePotionEffects()) {
                if (obj instanceof PotionEffect) {
                    PotionEffect e = (PotionEffect) obj;
                    if (e.getPotionID() == this.potionId) {
                        effect = new PotionEffect(this.potionId, this.duration + e.getDuration(), this.level);
                        break;
                    }
                }
            }
        } else {
            effect = new PotionEffect(this.potionId, this.duration, this.level);
        }

        if (effect != null) {
            effect.getCurativeItems()
                .clear();
            player.addPotionEffect(effect);
            WarpTheoryHelper
                .sendChat(player, "§5§o" + StatCollector.translateToLocal("chat.warptheory." + this.getName()));
        }
        return true;
    }
}
