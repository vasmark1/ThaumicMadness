package com.vasmark.thaumicmadness.compat.nei;

import java.awt.Rectangle;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.StatCollector;

import com.vasmark.thaumicmadness.compact.furnace.GuiCompactInfernalFurnace;
import com.vasmark.thaumicmadness.compact.furnace.TileCompactInfernalFurnace;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ItemApi;

@SideOnly(Side.CLIENT)
public class NEICompactFurnaceRecipeHandler extends TemplateRecipeHandler {

    public class CachedFurnaceRecipe extends CachedRecipe {

        public PositionedStack input;
        public PositionedStack result;
        public PositionedStack bonus;
        public boolean isCluster;

        public CachedFurnaceRecipe(ItemStack in, ItemStack out) {
            this.input = new PositionedStack(in, 48, 17);
            ItemStack finalOut = out.copy();
            this.isCluster = isCluster(in);
            if (this.isCluster) {
                finalOut.stackSize *= 2;
            }
            this.result = new PositionedStack(finalOut, 102, 17);
            ItemStack bonusStack = TileCompactInfernalFurnace.getSmeltingBonus(in, out);
            if (bonusStack != null) {
                this.bonus = new PositionedStack(bonusStack, 130, 17);
            }
        }

        @Override
        public PositionedStack getIngredient() {
            return this.input;
        }

        @Override
        public PositionedStack getResult() {
            return this.result;
        }

        @Override
        public PositionedStack getOtherStack() {
            return this.bonus;
        }
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("tile.thaumicmadness.compact_infernal_furnace.name");
    }

    @Override
    public String getGuiTexture() {
        return "textures/gui/container/furnace.png";
    }

    @Override
    public String getOverlayIdentifier() {
        return "compact_infernal_furnace";
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(70, 17, 24, 18), "compact_infernal_furnace"));
    }

    @Override
    public Class<? extends GuiContainer> getGuiClass() {
        return GuiCompactInfernalFurnace.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("compact_infernal_furnace") && getClass() == NEICompactFurnaceRecipeHandler.class) {
            Map<ItemStack, ItemStack> recipes = (Map<ItemStack, ItemStack>) FurnaceRecipes.smelting()
                .getSmeltingList();
            for (Map.Entry<ItemStack, ItemStack> recipe : recipes.entrySet()) {
                arecipes.add(new CachedFurnaceRecipe(recipe.getKey(), recipe.getValue()));
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadCraftingRecipes(ItemStack result) {
        Map<ItemStack, ItemStack> recipes = (Map<ItemStack, ItemStack>) FurnaceRecipes.smelting()
            .getSmeltingList();
        for (Map.Entry<ItemStack, ItemStack> recipe : recipes.entrySet()) {
            ItemStack out = recipe.getValue();
            if (NEIServerUtils.areStacksSameTypeCrafting(out, result)) {
                arecipes.add(new CachedFurnaceRecipe(recipe.getKey(), out));
            } else {
                ItemStack bonus = TileCompactInfernalFurnace.getSmeltingBonus(recipe.getKey(), out);
                if (bonus != null && NEIServerUtils.areStacksSameTypeCrafting(bonus, result)) {
                    arecipes.add(new CachedFurnaceRecipe(recipe.getKey(), out));
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void loadUsageRecipes(ItemStack ingredient) {
        Map<ItemStack, ItemStack> recipes = (Map<ItemStack, ItemStack>) FurnaceRecipes.smelting()
            .getSmeltingList();
        for (Map.Entry<ItemStack, ItemStack> recipe : recipes.entrySet()) {
            if (NEIServerUtils.areStacksSameTypeCrafting(recipe.getKey(), ingredient)) {
                arecipes.add(new CachedFurnaceRecipe(recipe.getKey(), recipe.getValue()));
            }
        }
    }

    @Override
    public void drawExtras(int recipe) {
        drawProgressBar(70, 17, 176, 14, 24, 17, 26, 0); // Furnace progress arrow (26 ticks)
        drawProgressBar(49, 36, 176, 0, 14, 14, 26, 7); // Flame

        CachedFurnaceRecipe r = (CachedFurnaceRecipe) arecipes.get(recipe);
        String sSpeed = "0.65с - 1.3с";
        Minecraft.getMinecraft().fontRenderer
            .drawString(sSpeed, 82 - Minecraft.getMinecraft().fontRenderer.getStringWidth(sSpeed) / 2, 38, 0x555555);
        if (r.bonus != null) {
            String sBonus = "Бонус: 30%-70%";
            Minecraft.getMinecraft().fontRenderer.drawString(
                sBonus,
                82 - Minecraft.getMinecraft().fontRenderer.getStringWidth(sBonus) / 2,
                48,
                0x885500);
        }
        if (r.isCluster) {
            String sCluster = "x2 Слитка";
            Minecraft.getMinecraft().fontRenderer.drawString(
                sCluster,
                82 - Minecraft.getMinecraft().fontRenderer.getStringWidth(sCluster) / 2,
                58,
                0x008800);
        }
    }

    private static boolean isCluster(ItemStack stack) {
        if (stack == null || stack.getItem() == null) return false;
        String unloc = stack.getItem()
            .getUnlocalizedName(stack);
        if (unloc != null && unloc.toLowerCase()
            .contains("cluster")) {
            return true;
        }
        ItemStack cluster = ItemApi.getItem("itemResource", 6);
        if (cluster != null && stack.getItem() == cluster.getItem()
            && stack.getItemDamage() >= 6
            && stack.getItemDamage() <= 12) {
            return true;
        }
        return false;
    }
}
