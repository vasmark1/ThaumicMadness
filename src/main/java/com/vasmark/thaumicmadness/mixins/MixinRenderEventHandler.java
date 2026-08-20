package com.vasmark.thaumicmadness.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.vasmark.thaumicmadness.client.AtlasRevealerHandler;
import com.vasmark.thaumicmadness.item.ModItems;

import thaumcraft.api.IGoggles;
import thaumcraft.client.lib.RenderEventHandler;

@Mixin(value = RenderEventHandler.class, remap = false)
public class MixinRenderEventHandler {

    @Redirect(
        method = "blockHighlight",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/player/InventoryPlayer;armorItemInSlot(I)Lnet/minecraft/item/ItemStack;",
            remap = true))
    private ItemStack mymodid$revealBlockPopups(InventoryPlayer inventory, int slot) {
        ItemStack realStack = inventory.armorItemInSlot(slot);
        if (slot == 3 && (realStack == null || !(realStack.getItem() instanceof IGoggles))) {
            if (AtlasRevealerHandler.hasThaumonomiconAtlas(Minecraft.getMinecraft().thePlayer)) {
                return new ItemStack(ModItems.itemThaumonomiconAtlas);
            }
        }
        return realStack;
    }
}
