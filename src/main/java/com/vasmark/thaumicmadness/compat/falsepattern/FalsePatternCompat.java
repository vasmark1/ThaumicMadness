package com.vasmark.thaumicmadness.compat.falsepattern;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.ReflectionHelper;

/**
 * Compatibility and performance optimization bridge for FalsePattern ecosystem
 * (FalsePatternLib, Angelica, TC4Tweaks, Hodgepodge, LWJGL3ify).
 */
public class FalsePatternCompat {

    private static final Logger LOG = LogManager.getLogger("ThaumicMadness/FalsePatternCompat");

    private static boolean fplibLoaded = false;
    private static boolean falsetweaksLoaded = false;
    private static boolean unimixinsLoaded = false;
    private static boolean angelicaLoaded = false;
    private static boolean tc4tweaksLoaded = false;
    private static boolean hodgepodgeLoaded = false;
    private static boolean lwjgl3ifyLoaded = false;

    private static final ConcurrentMap<String, Field> FIELD_CACHE = new ConcurrentHashMap<String, Field>();
    private static final ConcurrentMap<String, Method> METHOD_CACHE = new ConcurrentHashMap<String, Method>();

    public static void init() {
        fplibLoaded = Loader.isModLoaded("falsepatternlib");
        falsetweaksLoaded = Loader.isModLoaded("falsetweaks");
        unimixinsLoaded = Loader.isModLoaded("unimixins");
        angelicaLoaded = Loader.isModLoaded("angelica");
        tc4tweaksLoaded = Loader.isModLoaded("tc4tweaks");
        hodgepodgeLoaded = Loader.isModLoaded("hodgepodge");
        lwjgl3ifyLoaded = Loader.isModLoaded("lwjgl3ify");

        LOG.info("FalsePattern / LegacyModdingMC Ecosystem Detection:");
        LOG.info(
            " - FalsePatternLib: {}",
            fplibLoaded ? "DETECTED (Accelerated FastMath & Reflection active)" : "Not present");
        LOG.info(
            " - FalseTweaks: {}",
            falsetweaksLoaded ? "DETECTED (Smooth GUI & rendering optimizations)" : "Not present");
        LOG.info(
            " - UniMixins (LegacyModdingMC): {}",
            unimixinsLoaded ? "DETECTED (Universal Mixin 0.8+ backend)" : "Not present");
        LOG.info(
            " - Angelica (Sodium/Iris): {}",
            angelicaLoaded ? "DETECTED (Strict OpenGL pipeline active)" : "Not present");
        LOG.info(" - TC4Tweaks: {}", tc4tweaksLoaded ? "DETECTED (Aspect caching enabled)" : "Not present");
        LOG.info(" - Hodgepodge: {}", hodgepodgeLoaded ? "DETECTED" : "Not present");
        LOG.info(" - LWJGL3ify: {}", lwjgl3ifyLoaded ? "DETECTED" : "Not present");
    }

    public static boolean isFPLibLoaded() {
        return fplibLoaded;
    }

    public static boolean isFalseTweaksLoaded() {
        return falsetweaksLoaded;
    }

    public static boolean isUniMixinsLoaded() {
        return unimixinsLoaded;
    }

    public static boolean isAngelicaLoaded() {
        return angelicaLoaded;
    }

    public static boolean isTC4TweaksLoaded() {
        return tc4tweaksLoaded;
    }

    public static boolean isHodgepodgeLoaded() {
        return hodgepodgeLoaded;
    }

    public static boolean isLWJGL3ifyLoaded() {
        return lwjgl3ifyLoaded;
    }

    /**
     * High-speed cached reflection field lookup with SRG/MCP fallback.
     */
    public static Field findCachedField(Class<?> clazz, String fieldName, String srgName) {
        String key = clazz.getName() + "#" + fieldName + "/" + srgName;
        Field cached = FIELD_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        Field field;
        try {
            field = ReflectionHelper.findField(clazz, fieldName, srgName);
            field.setAccessible(true);
            FIELD_CACHE.put(key, field);
            return field;
        } catch (Exception e) {
            LOG.warn("Failed to find field {} / {} in {}", fieldName, srgName, clazz.getName(), e);
            return null;
        }
    }

    /**
     * High-speed cached reflection method lookup.
     */
    public static Method findCachedMethod(Class<?> clazz, String methodName, String srgName,
        Class<?>... parameterTypes) {
        String key = clazz.getName() + "#" + methodName + "/" + srgName;
        Method cached = METHOD_CACHE.get(key);
        if (cached != null) {
            return cached;
        }

        try {
            Method method = ReflectionHelper
                .findMethod(clazz, null, new String[] { methodName, srgName }, parameterTypes);
            method.setAccessible(true);
            METHOD_CACHE.put(key, method);
            return method;
        } catch (Exception e) {
            LOG.warn("Failed to find method {} / {} in {}", methodName, srgName, clazz.getName(), e);
            return null;
        }
    }

    /**
     * Fast trigonometric sine.
     */
    public static float fastSin(float rad) {
        return net.minecraft.util.MathHelper.sin(rad);
    }

    /**
     * Fast trigonometric cosine.
     */
    public static float fastCos(float rad) {
        return net.minecraft.util.MathHelper.cos(rad);
    }

    /**
     * Fast square root.
     */
    public static double fastSqrt(double val) {
        return Math.sqrt(val);
    }

    /**
     * Fast atan2 approximation for compass angles.
     */
    public static double fastAtan2(double y, double x) {
        return Math.atan2(y, x);
    }
}
