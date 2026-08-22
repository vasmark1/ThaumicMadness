package com.vasmark.thaumicmadness.warptheory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vasmark.thaumicmadness.warptheory.block.BlockPhantomDecay;
import com.vasmark.thaumicmadness.warptheory.block.TilePhantomDecay;
import com.vasmark.thaumicmadness.warptheory.entity.EntityPassiveFriend;
import com.vasmark.thaumicmadness.warptheory.events.IWarpEvent;
import com.vasmark.thaumicmadness.warptheory.events.WarpAcceleration;
import com.vasmark.thaumicmadness.warptheory.events.WarpBats;
import com.vasmark.thaumicmadness.warptheory.events.WarpBlink;
import com.vasmark.thaumicmadness.warptheory.events.WarpBlood;
import com.vasmark.thaumicmadness.warptheory.events.WarpBuff;
import com.vasmark.thaumicmadness.warptheory.events.WarpChests;
import com.vasmark.thaumicmadness.warptheory.events.WarpDecay;
import com.vasmark.thaumicmadness.warptheory.events.WarpEars;
import com.vasmark.thaumicmadness.warptheory.events.WarpFakeSound;
import com.vasmark.thaumicmadness.warptheory.events.WarpFakeSoundBehind;
import com.vasmark.thaumicmadness.warptheory.events.WarpFall;
import com.vasmark.thaumicmadness.warptheory.events.WarpFriend;
import com.vasmark.thaumicmadness.warptheory.events.WarpLightning;
import com.vasmark.thaumicmadness.warptheory.events.WarpLivestockRain;
import com.vasmark.thaumicmadness.warptheory.events.WarpRain;
import com.vasmark.thaumicmadness.warptheory.events.WarpSwamp;
import com.vasmark.thaumicmadness.warptheory.events.WarpTongue;
import com.vasmark.thaumicmadness.warptheory.events.WarpWind;
import com.vasmark.thaumicmadness.warptheory.events.WarpWither;
import com.vasmark.thaumicmadness.warptheory.items.ItemCursedParchment;
import com.vasmark.thaumicmadness.warptheory.items.ItemPureTear;
import com.vasmark.thaumicmadness.warptheory.items.ItemPurificationAmulet;
import com.vasmark.thaumicmadness.warptheory.items.ItemUnstableCatalyst;

import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import thaumcraft.api.IWarpingGear;
import thaumcraft.common.Thaumcraft;

public class WarpTheoryManager {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness/WarpTheory");
    private static final WarpTheoryManager INSTANCE = new WarpTheoryManager();

    public static WarpTheoryManager getInstance() {
        return INSTANCE;
    }

    // Items and Blocks
    public static ItemPureTear itemPureTear;
    public static ItemPurificationAmulet itemPurificationAmulet;
    public static ItemUnstableCatalyst itemUnstableCatalyst;
    public static ItemCursedParchment itemCursedParchment;
    public static BlockPhantomDecay blockPhantomDecay;

    // Events Registry & Decay mappings
    private final List<IWarpEvent> warpEvents = new ArrayList<IWarpEvent>();
    private final Map<Block, Block> decayMappings = new HashMap<Block, Block>();
    private final Map<String, List<IWarpEvent>> playerEventQueue = new ConcurrentHashMap<String, List<IWarpEvent>>();

    public void preInit() {
        // Register Items
        itemPureTear = new ItemPureTear();
        GameRegistry.registerItem(itemPureTear, "pure_tear");

        itemPurificationAmulet = new ItemPurificationAmulet();
        GameRegistry.registerItem(itemPurificationAmulet, "purification_amulet");

        itemUnstableCatalyst = new ItemUnstableCatalyst();
        GameRegistry.registerItem(itemUnstableCatalyst, "unstable_catalyst");

        itemCursedParchment = new ItemCursedParchment();
        GameRegistry.registerItem(itemCursedParchment, "cursed_parchment");

        // Register Blocks & Tiles
        blockPhantomDecay = new BlockPhantomDecay();
        GameRegistry.registerBlock(blockPhantomDecay, "phantom_decay");
        GameRegistry.registerTileEntity(TilePhantomDecay.class, "thaumicmadness:phantom_decay");

        // Register Entities
        EntityRegistry.registerGlobalEntityID(
            EntityPassiveFriend.class,
            "thaumicmadness.friend",
            EntityRegistry.findGlobalUniqueEntityId(),
            0x0DA70B,
            0x1B5E20);

        initDecayMappings();
        initWarpEvents();
        LOGGER.info("Warp Theory subsystem pre-initialized with {} events.", warpEvents.size());
    }

    private void initDecayMappings() {
        decayMappings.put(Blocks.grass, Blocks.dirt);
        decayMappings.put(Blocks.dirt, Blocks.sand);
        decayMappings.put(Blocks.stone, Blocks.cobblestone);
        decayMappings.put(Blocks.cobblestone, Blocks.gravel);
        decayMappings.put(Blocks.sandstone, Blocks.sand);
        decayMappings.put(Blocks.gravel, Blocks.sand);
        decayMappings.put(Blocks.sand, Blocks.air);
        decayMappings.put(Blocks.lava, Blocks.cobblestone);
        decayMappings.put(Blocks.flowing_lava, Blocks.cobblestone);
        decayMappings.put(Blocks.water, Blocks.air);
        decayMappings.put(Blocks.snow, Blocks.water);
        decayMappings.put(Blocks.snow_layer, Blocks.air);
        decayMappings.put(Blocks.ice, Blocks.water);
        decayMappings.put(Blocks.clay, Blocks.sand);
        decayMappings.put(Blocks.mycelium, Blocks.grass);
        decayMappings.put(Blocks.stained_hardened_clay, Blocks.hardened_clay);
        decayMappings.put(Blocks.hardened_clay, Blocks.clay);
        decayMappings.put(Blocks.coal_ore, Blocks.stone);
        decayMappings.put(Blocks.diamond_ore, Blocks.stone);
        decayMappings.put(Blocks.emerald_ore, Blocks.stone);
        decayMappings.put(Blocks.gold_ore, Blocks.stone);
        decayMappings.put(Blocks.iron_ore, Blocks.stone);
        decayMappings.put(Blocks.lapis_ore, Blocks.stone);
        decayMappings.put(Blocks.redstone_ore, Blocks.stone);
        decayMappings.put(Blocks.lit_redstone_ore, Blocks.stone);
        decayMappings.put(Blocks.quartz_ore, Blocks.netherrack);
        decayMappings.put(Blocks.netherrack, Blocks.cobblestone);
        decayMappings.put(Blocks.soul_sand, Blocks.sand);
        decayMappings.put(Blocks.glowstone, Blocks.cobblestone);
        decayMappings.put(Blocks.log, Blocks.dirt);
        decayMappings.put(Blocks.log2, Blocks.dirt);
        decayMappings.put(Blocks.brown_mushroom_block, Blocks.dirt);
        decayMappings.put(Blocks.red_mushroom_block, Blocks.dirt);
        decayMappings.put(Blocks.end_stone, Blocks.cobblestone);
        decayMappings.put(Blocks.obsidian, Blocks.cobblestone);
    }

    private void initWarpEvents() {
        warpEvents.add(new WarpBats());
        warpEvents.add(new WarpBlink());
        warpEvents.add(new WarpBuff("poison", 16, new PotionEffect(Potion.poison.id, 400)));
        warpEvents.add(new WarpBuff("nausea", 25, new PotionEffect(Potion.confusion.id, 400)));
        warpEvents.add(new WarpBuff("jump", 18, new PotionEffect(Potion.jump.id, 400, 20)));
        warpEvents.add(new WarpBuff("blind", 43, new PotionEffect(Potion.blindness.id, 400)));
        warpEvents.add(new WarpDecay());
        warpEvents.add(new WarpEars());
        warpEvents.add(new WarpSwamp());
        warpEvents.add(new WarpTongue());
        warpEvents.add(new WarpFriend());
        warpEvents.add(new WarpLivestockRain());
        warpEvents.add(new WarpWind());
        warpEvents.add(new WarpChests());
        warpEvents.add(new WarpBlood());
        warpEvents.add(new WarpAcceleration());
        warpEvents.add(new WarpLightning());
        warpEvents.add(new WarpFall());
        warpEvents.add(new WarpRain());
        warpEvents.add(new WarpWither());
        warpEvents.add(new WarpFakeSound("fakeexplosion", "random.explode", 8));
        warpEvents.add(new WarpFakeSoundBehind("fakecreeper", "creeper.primed", 2));
    }

    public Block getDecayBlock(Block original) {
        return decayMappings.get(original);
    }

    // Direct, robust integration with Thaumcraft PlayerKnowledge
    public static int getTotalWarp(EntityPlayer player) {
        if (player == null) return 0;
        String name = player.getCommandSenderName();
        try {
            int perm = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpPerm(name);
            int sticky = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpSticky(name);
            int temp = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpTemp(name);
            return perm + sticky + temp + getWarpFromGear(player);
        } catch (Throwable t) {
            return 0;
        }
    }

    public static int[] getIndividualWarps(EntityPlayer player) {
        if (player == null) return new int[] { 0, 0, 0 };
        String name = player.getCommandSenderName();
        try {
            int perm = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpPerm(name);
            int sticky = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpSticky(name);
            int temp = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpTemp(name);
            return new int[] { perm, sticky, temp };
        } catch (Throwable t) {
            return new int[] { 0, 0, 0 };
        }
    }

    public static int getWarpFromGear(EntityPlayer player) {
        if (player == null || player.inventory == null) return 0;
        int warp = 0;
        for (ItemStack item : player.inventory.armorInventory) {
            if (item != null && item.getItem() instanceof IWarpingGear) {
                warp += ((IWarpingGear) item.getItem()).getWarp(item, player);
            }
        }
        for (ItemStack item : player.inventory.mainInventory) {
            if (item != null && item.getItem() instanceof IWarpingGear) {
                warp += ((IWarpingGear) item.getItem()).getWarp(item, player);
            }
        }
        return warp;
    }

    public static void purgeWarp(EntityPlayer player) {
        int total = getTotalWarp(player);
        queueMultipleEvents(player, total);
        removeWarp(player, total);
    }

    public static void removeWarp(EntityPlayer player, int amount) {
        if (player == null || amount <= 0) return;
        String name = player.getCommandSenderName();
        try {
            int temp = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpTemp(name);
            if (amount <= temp) {
                Thaumcraft.proxy.getPlayerKnowledge()
                    .setWarpTemp(name, temp - amount);
                return;
            }
            Thaumcraft.proxy.getPlayerKnowledge()
                .setWarpTemp(name, 0);
            amount -= temp;

            int sticky = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpSticky(name);
            if (amount <= sticky) {
                Thaumcraft.proxy.getPlayerKnowledge()
                    .setWarpSticky(name, sticky - amount);
                return;
            }
            Thaumcraft.proxy.getPlayerKnowledge()
                .setWarpSticky(name, 0);
            amount -= sticky;

            int perm = Thaumcraft.proxy.getPlayerKnowledge()
                .getWarpPerm(name);
            if (amount <= perm) {
                Thaumcraft.proxy.getPlayerKnowledge()
                    .setWarpPerm(name, perm - amount);
            } else {
                Thaumcraft.proxy.getPlayerKnowledge()
                    .setWarpPerm(name, 0);
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to update warp for player {}", name, t);
        }
    }

    public static int queueMultipleEvents(EntityPlayer player, int amount) {
        int remaining = amount;
        while (remaining > 0) {
            IWarpEvent event = queueOneEvent(player, remaining);
            if (event == null) break;
            remaining -= event.getCost();
        }
        return remaining;
    }

    public static IWarpEvent queueOneEvent(EntityPlayer player, int maxSeverity) {
        IWarpEvent event = getAppropriateEvent(player, maxSeverity);
        if (event != null) {
            queueEvent(player, event);
        }
        return event;
    }

    public static void queueEvent(EntityPlayer player, IWarpEvent event) {
        if (player == null || event == null) return;
        String name = player.getCommandSenderName();
        List<IWarpEvent> queue = INSTANCE.playerEventQueue.get(name);
        if (queue == null) {
            queue = new ArrayList<IWarpEvent>();
            INSTANCE.playerEventQueue.put(name, queue);
        }
        queue.add(event);
    }

    public static IWarpEvent dequeueEvent(EntityPlayer player) {
        if (player == null) return null;
        String name = player.getCommandSenderName();
        List<IWarpEvent> queue = INSTANCE.playerEventQueue.get(name);
        if (queue == null || queue.isEmpty()) return null;
        return queue.remove(0);
    }

    public static IWarpEvent getAppropriateEvent(EntityPlayer player, int maxSeverity) {
        List<IWarpEvent> shuffled = new ArrayList<IWarpEvent>(INSTANCE.warpEvents);
        Collections.shuffle(shuffled);
        for (IWarpEvent e : shuffled) {
            if (e.getSeverity() <= maxSeverity && e.canDo(player)) {
                return e;
            }
        }
        return null;
    }
}
