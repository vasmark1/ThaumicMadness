package com.vasmark.thaumicmadness.mixins;

import net.minecraft.entity.player.EntityPlayer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.vasmark.thaumicmadness.client.AtlasRevealerHandler;

import tuhljin.automagy.lib.events.AutomagyRenderEventHandler;

@Mixin(value = AutomagyRenderEventHandler.class, remap = false)
public class MixinAutomagyRenderEventHandler {

    @Inject(method = "shouldShowAspectsToPlayer", at = @At("HEAD"), cancellable = true)
    private void mymodid$automagyAspects(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (AtlasRevealerHandler.hasThaumonomiconAtlas(player)) {
            cir.setReturnValue(true);
        }
    }
}
