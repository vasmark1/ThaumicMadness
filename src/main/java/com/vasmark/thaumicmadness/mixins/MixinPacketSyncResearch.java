package com.vasmark.thaumicmadness.mixins;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;

@Mixin(value = PacketSyncResearch.class, remap = false)
public class MixinPacketSyncResearch {

    @Shadow
    protected ArrayList<String> data;

    @Inject(method = "<init>(Lnet/minecraft/entity/player/EntityPlayer;)V", at = @At("RETURN"))
    private void thaumicmadness$cloneDataSafely(EntityPlayer player, CallbackInfo ci) {
        if (this.data != null) {
            synchronized (this.data) {
                this.data = new ArrayList<String>(this.data);
            }
        }
    }
}
