package com.vasmark.thaumicmadness.mixins;

import java.util.List;

import net.minecraft.client.Minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.vasmark.thaumicmadness.compat.journeymap.AuraNodeDrawStep;
import com.vasmark.thaumicmadness.compat.journeymap.JourneyMapCompat;

import journeymap.client.model.MapState;
import journeymap.client.properties.InGameMapProperties;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.draw.RadarDrawStepFactory;
import journeymap.client.render.draw.WaypointDrawStepFactory;
import journeymap.client.render.map.GridRenderer;

@Mixin(value = MapState.class, remap = false)
public class MixinMapState {

    @Shadow
    protected List<DrawStep> drawStepList;

    @Inject(method = "generateDrawSteps", at = @At("RETURN"))
    private void thaumicmadness$injectAuraNodeDrawSteps(Minecraft mc, GridRenderer gridRenderer,
        WaypointDrawStepFactory waypointRenderer, RadarDrawStepFactory radarRenderer, InGameMapProperties mapProperties,
        float drawScale, boolean refresh, CallbackInfo ci) {
        if (JourneyMapCompat.isJourneyMapLoaded() && this.drawStepList != null) {
            this.drawStepList.add(new AuraNodeDrawStep());
        }
    }
}
