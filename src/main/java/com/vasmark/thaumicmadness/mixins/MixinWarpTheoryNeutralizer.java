package com.vasmark.thaumicmadness.mixins;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;

@Pseudo
@Mixin(targets = "shukaro.warptheory.WarpTheory", remap = false)
public class MixinWarpTheoryNeutralizer {

    private static final Logger LOG = LogManager.getLogger("ThaumicMadness/WarpTheoryNeutralizer");

    @Inject(method = "preInit", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelPreInit(FMLPreInitializationEvent event, CallbackInfo ci) {
        LOG.warn("=========================================================================================");
        LOG.warn("[ThaumicMadness] Standalone mod 'WarpTheory' detected on classpath and safely neutralized!");
        LOG.warn(
            "[ThaumicMadness] All Warp Theory items, events, and research are natively provided by Thaumic Madness.");
        LOG.warn("=========================================================================================");
        ci.cancel();
    }

    @Inject(method = "init", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelInit(FMLInitializationEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "postInit", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelPostInit(FMLPostInitializationEvent event, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "serverStarting", at = @At("HEAD"), cancellable = true, remap = false)
    private void cancelServerStarting(FMLServerStartingEvent event, CallbackInfo ci) {
        ci.cancel();
    }
}
