package com.vasmark.thaumicmadness.mixins;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

/**
 * UniMixins & SpongeMixin configuration plugin for Thaumic Madness.
 * Dynamically applies mixins only when target optional mod classes are present on the classpath.
 */
public class ThaumicMadnessMixinPlugin implements IMixinConfigPlugin {

    private static final Logger LOG = LogManager.getLogger("ThaumicMadness/MixinPlugin");

    @Override
    public void onLoad(String mixinPackage) {
        LOG.info("Initializing Thaumic Madness UniMixins config plugin for package: {}", mixinPackage);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Gadomancy mixins
        if (mixinClassName.contains("MixinRenderTileNodeBasic") || targetClassName.contains("makeo.gadomancy")) {
            boolean loaded = isClassPresent("makeo.gadomancy.client.renderers.tile.RenderTileNodeBasic");
            if (!loaded) {
                LOG.debug("Skipping {} as Gadomancy is not installed.", mixinClassName);
            }
            return loaded;
        }

        // Automagy mixins
        if (mixinClassName.contains("MixinAutomagyRenderEventHandler")
            || targetClassName.contains("tuhljin.automagy")) {
            boolean loaded = isClassPresent("tuhljin.automagy.client.gui.AutomagyRenderEventHandler");
            if (!loaded) {
                LOG.debug("Skipping {} as Automagy is not installed.", mixinClassName);
            }
            return loaded;
        }

        // Thaumores mixins
        if (mixinClassName.contains("MixinTileInfusedBlockOreRenderer") || targetClassName.contains("thaumores")) {
            boolean loaded = isClassPresent("mjaroslav.mcmods.thaumores.client.render.TileInfusedBlockOreRenderer");
            if (!loaded) {
                LOG.debug("Skipping {} as Thaumores is not installed.", mixinClassName);
            }
            return loaded;
        }

        // JourneyMap mixins
        if (mixinClassName.contains("MixinMapState") || targetClassName.contains("journeymap")) {
            boolean loaded = isClassPresent("journeymap.client.model.MapState");
            if (!loaded) {
                LOG.debug("Skipping {} as JourneyMap is not installed.", mixinClassName);
            }
            return loaded;
        }

        // WarpTheory standalone neutralizer
        if (mixinClassName.contains("MixinWarpTheoryNeutralizer") || targetClassName.contains("shukaro.warptheory")) {
            boolean loaded = isClassPresent("shukaro.warptheory.WarpTheory");
            if (!loaded) {
                LOG.debug("Skipping {} as standalone WarpTheory is not present.", mixinClassName);
            }
            return loaded;
        }

        // TCNodeTracker standalone neutralizer
        if (mixinClassName.contains("MixinTCNodeTrackerNeutralizer")
            || targetClassName.contains("com.dyonovan.tcnodetracker")) {
            boolean loaded = isClassPresent("com.dyonovan.tcnodetracker.TCNodeTracker");
            if (!loaded) {
                LOG.debug("Skipping {} as standalone TCNodeTracker is not present.", mixinClassName);
            }
            return loaded;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {}

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    private static boolean isClassPresent(String className) {
        try {
            return net.minecraft.launchwrapper.Launch.classLoader.getResource(className.replace('.', '/') + ".class")
                != null;
        } catch (Throwable ignored) {
            return false;
        }
    }
}
