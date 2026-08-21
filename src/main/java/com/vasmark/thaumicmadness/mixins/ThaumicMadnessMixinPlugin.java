package com.vasmark.thaumicmadness.mixins;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import cpw.mods.fml.common.Loader;

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
            boolean loaded = isModLoaded("gadomancy");
            if (!loaded) {
                LOG.debug("Skipping {} as Gadomancy is not installed.", mixinClassName);
            }
            return loaded;
        }

        // Automagy mixins
        if (mixinClassName.contains("MixinAutomagyRenderEventHandler")
            || targetClassName.contains("tuhljin.automagy")) {
            boolean loaded = isModLoaded("Automagy");
            if (!loaded) {
                LOG.debug("Skipping {} as Automagy is not installed.", mixinClassName);
            }
            return loaded;
        }

        // Thaumores mixins
        if (mixinClassName.contains("MixinTileInfusedBlockOreRenderer") || targetClassName.contains("thaumores")) {
            boolean loaded = isModLoaded("Thaumores");
            if (!loaded) {
                LOG.debug("Skipping {} as Thaumores is not installed.", mixinClassName);
            }
            return loaded;
        }

        // JourneyMap mixins
        if (mixinClassName.contains("MixinMapState") || targetClassName.contains("journeymap")) {
            boolean loaded = isModLoaded("journeymap") || isModLoaded("JourneyMap");
            if (!loaded) {
                LOG.debug("Skipping {} as JourneyMap is not installed.", mixinClassName);
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

    private static boolean isModLoaded(String modId) {
        try {
            return Loader.isModLoaded(modId);
        } catch (Throwable ignored) {
            return false;
        }
    }
}
