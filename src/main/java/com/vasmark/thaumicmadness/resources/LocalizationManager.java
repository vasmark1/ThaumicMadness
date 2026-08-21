package com.vasmark.thaumicmadness.resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.Locale;
import net.minecraft.util.StringTranslate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LocalizationManager implements IResourceManagerReloadListener {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-Localization");
    private static final LocalizationManager INSTANCE = new LocalizationManager();

    private static final String[] LANG_PATHS = new String[] { "/assets/thaumicmadness/lang/ru_RU.lang",
        "/assets/witchinggadgets/lang/ru_RU.lang", "/assets/thaumicbases/lang/ru_RU.lang",
        "/assets/thaumicexploration/lang/ru_RU.lang", "/assets/advthaum/lang/ru_RU.lang",
        "/assets/gadomancy/lang/ru_RU.lang", "/assets/warptheory/lang/ru_RU.lang",
        "/assets/thaumicinsurgence/lang/ru_RU.lang", "/assets/tcnodetracker/lang/ru_RU.lang",
        "/assets/tcinventoryscan/lang/ru_RU.lang", "/assets/alchgrate/lang/ru_RU.lang",
        "/assets/benway_knowledge/lang/ru_RU.lang" };

    private final Map<String, String> cachedTranslations = new HashMap<>();
    private boolean loaded = false;
    private boolean reloadListenerRegistered = false;

    public static LocalizationManager getInstance() {
        return INSTANCE;
    }

    public synchronized void loadAllTranslations() {
        cachedTranslations.clear();
        for (String path : LANG_PATHS) {
            loadFromClasspath(path);
        }
        loaded = true;
        LOGGER.info("Loaded {} Russian translation keys from addon lang files.", cachedTranslations.size());
    }

    private void loadFromClasspath(String path) {
        try (InputStream is = LocalizationManager.class.getResourceAsStream(path)) {
            if (is == null) {
                LOGGER.warn("Could not find lang file on classpath: {}", path);
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#") || line.startsWith("-")) {
                        continue;
                    }
                    int equalsIndex = line.indexOf('=');
                    if (equalsIndex > 0) {
                        String key = line.substring(0, equalsIndex)
                            .trim();
                        String value = line.substring(equalsIndex + 1);
                        cachedTranslations.put(key, value);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read lang file: " + path, e);
        }
    }

    public void injectAll() {
        if (!loaded) {
            loadAllTranslations();
        }

        // 1. Inject into StringTranslate singleton (used by StatCollector.translateToLocal)
        injectStringTranslate();

        // 2. Inject into LanguageRegistry for ru_RU
        try {
            LanguageRegistry.instance()
                .injectLanguage("ru_RU", (HashMap<String, String>) cachedTranslations);
        } catch (Throwable t) {
            LOGGER.warn("Could not inject into LanguageRegistry: {}", t.getMessage());
        }

        // 3. Inject into Client Locale.properties if on client
        if (FMLCommonHandler.instance()
            .getEffectiveSide() == Side.CLIENT) {
            injectClientLocale();
            registerReloadListenerIfNeeded();
        }
    }

    @SuppressWarnings("unchecked")
    private void injectStringTranslate() {
        try {
            StringTranslate st = null;
            for (Field field : StringTranslate.class.getDeclaredFields()) {
                if (StringTranslate.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    st = (StringTranslate) field.get(null);
                    if (st != null) break;
                }
            }
            if (st == null) {
                for (java.lang.reflect.Method m : StringTranslate.class.getDeclaredMethods()) {
                    if (m.getParameterTypes().length == 0
                        && StringTranslate.class.isAssignableFrom(m.getReturnType())) {
                        m.setAccessible(true);
                        st = (StringTranslate) m.invoke(null);
                        if (st != null) break;
                    }
                }
            }

            if (st != null) {
                for (Field field : StringTranslate.class.getDeclaredFields()) {
                    if (Map.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        Map<String, String> map = (Map<String, String>) field.get(st);
                        if (map != null) {
                            map.putAll(cachedTranslations);
                            LOGGER
                                .debug("Successfully injected {} keys into StringTranslate", cachedTranslations.size());
                            break;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to inject into StringTranslate: {}", t.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void injectClientLocale() {
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null || mc.getLanguageManager() == null) {
                return;
            }

            Locale currentLocale = null;
            for (Field field : mc.getLanguageManager()
                .getClass()
                .getDeclaredFields()) {
                if (Locale.class.isAssignableFrom(field.getType())) {
                    field.setAccessible(true);
                    currentLocale = (Locale) field.get(mc.getLanguageManager());
                    break;
                }
            }

            if (currentLocale != null) {
                for (Field field : Locale.class.getDeclaredFields()) {
                    if (Map.class.isAssignableFrom(field.getType())) {
                        field.setAccessible(true);
                        Map<String, String> properties = (Map<String, String>) field.get(currentLocale);
                        if (properties != null) {
                            properties.putAll(cachedTranslations);
                            LOGGER.debug(
                                "Successfully injected {} keys into Locale.properties",
                                cachedTranslations.size());
                            break;
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to inject into Client Locale: {}", t.getMessage());
        }
    }

    private void registerReloadListenerIfNeeded() {
        if (reloadListenerRegistered) {
            return;
        }
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc != null && mc.getResourceManager() instanceof IReloadableResourceManager) {
                IReloadableResourceManager rm = (IReloadableResourceManager) mc.getResourceManager();
                rm.registerReloadListener(this);
                reloadListenerRegistered = true;
                LOGGER.info("Registered LocalizationManager as IResourceManagerReloadListener.");
            }
        } catch (Throwable t) {
            LOGGER.warn("Failed to register reload listener: {}", t.getMessage());
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        LOGGER.info("ResourceManager reloaded - re-injecting all translations.");
        injectAll();
    }
}
