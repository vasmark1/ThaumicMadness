package com.vasmark.thaumicmadness.compat.bloodmagic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.wands.StaffRod;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;

public class BloodMagicItems {

    public static Item itemWandRodBlood;
    public static Item itemWandCapBlood;
    public static Item itemFocusBloodSacrifice;
    public static Item itemBloodRing;
    public static Item itemBloodAmulet;

    public static WandRod WAND_ROD_BLOOD;
    public static StaffRod STAFF_ROD_BLOOD;
    public static WandCap WAND_CAP_BLOOD;

    public static void init() {
        itemWandRodBlood = new ItemWandRodBlood();
        GameRegistry.registerItem(itemWandRodBlood, "wand_rod_blood");

        itemWandCapBlood = new ItemWandCapBlood();
        GameRegistry.registerItem(itemWandCapBlood, "wand_cap_blood");

        itemFocusBloodSacrifice = new ItemFocusBloodSacrifice();
        GameRegistry.registerItem(itemFocusBloodSacrifice, "focus_blood_sacrifice");

        itemBloodRing = new ItemBloodRing();
        GameRegistry.registerItem(itemBloodRing, "blood_ring");

        itemBloodAmulet = new ItemBloodAmulet();
        GameRegistry.registerItem(itemBloodAmulet, "blood_amulet");

        // Register with Thaumcraft API
        WAND_ROD_BLOOD = new WandRod(
            "blood",
            150,
            new ItemStack(itemWandRodBlood, 1, 0),
            12,
            new ResourceLocation("mymodid", "textures/models/wand_rod_blood.png"));

        STAFF_ROD_BLOOD = new StaffRod(
            "blood_staff",
            250,
            new ItemStack(itemWandRodBlood, 1, 1),
            22,
            new ResourceLocation("mymodid", "textures/models/wand_rod_blood.png"));

        WAND_CAP_BLOOD = new WandCap("blood", 0.85F, new ItemStack(itemWandCapBlood, 1, 0), 9);
        WAND_CAP_BLOOD.setTexture(new ResourceLocation("mymodid", "textures/models/wand_cap_blood.png"));

        // Register event listener for LP-powered wand recharge
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(new SanguineWandRechargeHandler());
    }
}
