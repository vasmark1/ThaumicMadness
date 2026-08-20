package com.vasmark.thaumicmadness.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.vasmark.thaumicmadness.client.AtlasRevealerHandler;
import com.vasmark.thaumicmadness.item.ModItems;

import mjaroslav.mcmods.thaumores.client.render.tile.TileInfusedBlockOreRenderer;
import thaumcraft.api.IGoggles;

@Mixin(value = TileInfusedBlockOreRenderer.class, remap = false)
public class MixinTileInfusedBlockOreRenderer {

    @Redirect(
        method = "renderTileEntityAt",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;armorItemInSlot(I)Lnet/minecraft/item/ItemStack;",
            remap = true))
    private ItemStack mymodid$revealInfusedOres(InventoryPlayer inventory, int slot) {
        ItemStack realStack = inventory.armorItemInSlot(slot);
        if (slot == 3 && (realStack == null || !(realStack.getItem() instanceof IGoggles))) {
            if (AtlasRevealerHandler.hasThaumonomiconAtlas(Minecraft.getMinecraft().thePlayer)) {
                return new ItemStack(ModItems.itemThaumonomiconAtlas);
            }
        }
        return realStack;
    }
}
