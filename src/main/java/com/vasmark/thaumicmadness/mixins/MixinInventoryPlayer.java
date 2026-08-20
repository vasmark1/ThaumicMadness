package com.vasmark.thaumicmadness.mixins;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.vasmark.thaumicmadness.client.AtlasRevealerHandler;
import com.vasmark.thaumicmadness.item.ModItems;

@Mixin(InventoryPlayer.class)
public class MixinInventoryPlayer {

    @Shadow
    public ItemStack[] armorInventory;

    @Shadow
    public EntityPlayer player;

    @Inject(method = "armorItemInSlot", at = @At("HEAD"), cancellable = true)
    private void mymodid$grantGogglesFromAtlas(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (slot == 3 && this.armorInventory != null && this.armorInventory[3] == null) {
            if (this.player != null && this.player.worldObj != null && this.player.worldObj.isRemote) {
                if (AtlasRevealerHandler.hasThaumonomiconAtlas(this.player)) {
                    if (ModItems.itemThaumonomiconAtlas != null) {
                        cir.setReturnValue(new ItemStack(ModItems.itemThaumonomiconAtlas));
                    }
                }
            }
        }
    }
}
