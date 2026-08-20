package com.vasmark.thaumicmadness.resources;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BuiltinLocalizationResourcePack implements IResourcePack {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-LocalizationPack");
    private static boolean registered = false;

    private static final Set<String> DOMAINS = new HashSet<>();

    static {
        DOMAINS.add("thaumicmadness");
        DOMAINS.add("thaumicbases");
        DOMAINS.add("witchinggadgets");
        DOMAINS.add("thaumicexploration");
        DOMAINS.add("advthaum");
        DOMAINS.add("gadomancy");
        DOMAINS.add("warptheory");
        DOMAINS.add("thaumicinsurgence");
        DOMAINS.add("tcnodetracker");
        DOMAINS.add("tcinventoryscan");
        DOMAINS.add("alchgrate");
        DOMAINS.add("benway_knowledge");
    }

    public static void register() {
        if (registered) return;
        try {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc == null) return;

            BuiltinLocalizationResourcePack pack = new BuiltinLocalizationResourcePack();

            // Inject into Minecraft.defaultResourcePacks
            List<IResourcePack> defaultPacks = null;
            for (Field f : Minecraft.class.getDeclaredFields()) {
                if (List.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    Object val = f.get(mc);
                    if (val instanceof List) {
                        List<?> list = (List<?>) val;
                        if (list.isEmpty() || list.get(0) instanceof IResourcePack) {
                            defaultPacks = (List<IResourcePack>) list;
                            break;
                        }
                    }
                }
            }

            if (defaultPacks != null) {
                defaultPacks.add(pack);
                registered = true;
                LOGGER.info(
                    "Successfully registered built-in non-removable localization resource pack: " + pack.getPackName());
                mc.refreshResources();
            } else {
                LOGGER.warn("Could not locate defaultResourcePacks field in Minecraft class.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to register built-in localization resource pack: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream getInputStream(ResourceLocation location) throws IOException {
        String path = "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
        InputStream is = BuiltinLocalizationResourcePack.class.getResourceAsStream(path);
        if (is != null) {
            return is;
        }
        throw new FileNotFoundException("Resource not found in built-in pack: " + location);
    }

    @Override
    public boolean resourceExists(ResourceLocation location) {
        if (!DOMAINS.contains(location.getResourceDomain())) {
            return false;
        }
        String path = "/assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
        InputStream is = BuiltinLocalizationResourcePack.class.getResourceAsStream(path);
        if (is != null) {
            try {
                is.close();
            } catch (IOException ignored) {}
            return true;
        }
        return false;
    }

    @Override
    public Set getResourceDomains() {
        return DOMAINS;
    }

    @Override
    public IMetadataSection getPackMetadata(IMetadataSerializer serializer, String section) throws IOException {
        if ("pack".equals(section)) {
            return new PackMetadataSection(
                new ChatComponentText("Встроенный перевод таумкрафт аддонов на русский язык"),
                1);
        }
        return null;
    }

    @Override
    public BufferedImage getPackImage() throws IOException {
        return null;
    }

    @Override
    public String getPackName() {
        return "Thaumic Addons Russian Localization";
    }
}
