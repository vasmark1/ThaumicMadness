package com.vasmark.thaumicmadness.compat.journeymap;

import java.awt.Color;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vasmark.thaumicmadness.Config;
import com.vasmark.thaumicmadness.nodetracker.NodeData;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import journeymap.client.model.Waypoint;
import journeymap.client.waypoint.WaypointStore;

@SideOnly(Side.CLIENT)
public class JourneyMapCompat {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-JourneyMap");
    private static Boolean jmLoaded = null;

    public static boolean isJourneyMapLoaded() {
        if (jmLoaded == null) {
            try {
                if (Loader.isModLoaded("journeymap") || Loader.isModLoaded("JourneyMap")) {
                    Class.forName("journeymap.client.waypoint.WaypointStore");
                    jmLoaded = true;
                    LOGGER.info("JourneyMap detected and initialized for Atlas Node Tracker.");
                } else {
                    jmLoaded = false;
                }
            } catch (Throwable t) {
                LOGGER.warn("JourneyMap classes not found or incompatible: {}", t.getMessage());
                jmLoaded = false;
            }
        }
        return jmLoaded != null && jmLoaded;
    }

    public static boolean hasWaypoint(NodeData node) {
        if (!isJourneyMapLoaded() || node == null) return false;
        try {
            return findWaypoint(node) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    public static Waypoint findWaypoint(NodeData node) {
        if (!isJourneyMapLoaded() || node == null) return null;
        try {
            Collection<Waypoint> all = WaypointStore.instance()
                .getAll();
            if (all == null) return null;
            for (Waypoint wp : all) {
                if (wp.getX() == node.x && wp.getY() == node.y && wp.getZ() == node.z) {
                    Collection<Integer> dims = wp.getDimensions();
                    if (dims == null || dims.isEmpty() || dims.contains(node.dim)) {
                        return wp;
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.error("Error finding JourneyMap waypoint: {}", t.getMessage());
        }
        return null;
    }

    public static boolean toggleWaypoint(NodeData node) {
        if (!isJourneyMapLoaded() || node == null) return false;
        try {
            Waypoint existing = findWaypoint(node);
            if (existing != null) {
                WaypointStore.instance()
                    .remove(existing);
                return false; // Removed
            } else {
                createOrUpdateWaypoint(node);
                return true; // Added
            }
        } catch (Throwable t) {
            LOGGER.error("Error toggling JourneyMap waypoint: {}", t.getMessage());
            return false;
        }
    }

    public static boolean createOrUpdateWaypoint(NodeData node) {
        if (!isJourneyMapLoaded() || node == null) return false;
        try {
            Waypoint existing = findWaypoint(node);
            if (existing != null) {
                WaypointStore.instance()
                    .remove(existing);
            }

            String name = buildWaypointName(node);
            Color color = getNodeColor(node);
            Waypoint wp = new Waypoint(name, node.x, node.y, node.z, color, Waypoint.Type.Normal, node.dim);
            WaypointStore.instance()
                .save(wp);
            LOGGER.debug("Created JourneyMap waypoint '{}' at ({}, {}, {})", name, node.x, node.y, node.z);
            return true;
        } catch (Throwable t) {
            LOGGER.error("Failed to create JourneyMap waypoint: {}", t.getMessage());
            return false;
        }
    }

    public static boolean removeWaypoint(NodeData node) {
        if (!isJourneyMapLoaded() || node == null) return false;
        try {
            Waypoint existing = findWaypoint(node);
            if (existing != null) {
                WaypointStore.instance()
                    .remove(existing);
                return true;
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to remove JourneyMap waypoint: {}", t.getMessage());
        }
        return false;
    }

    public static int syncAllWaypoints(List<NodeData> nodes) {
        if (!isJourneyMapLoaded() || nodes == null || nodes.isEmpty()) return 0;
        int count = 0;
        for (NodeData node : nodes) {
            if (createOrUpdateWaypoint(node)) {
                count++;
            }
        }
        return count;
    }

    public static int removeAllWaypoints(List<NodeData> nodes) {
        if (!isJourneyMapLoaded() || nodes == null || nodes.isEmpty()) return 0;
        int count = 0;
        for (NodeData node : nodes) {
            if (removeWaypoint(node)) {
                count++;
            }
        }
        return count;
    }

    public static void onNodeScanned(NodeData node) {
        if (isJourneyMapLoaded() && Config.enableJourneyMapAutoWaypoint && node != null) {
            createOrUpdateWaypoint(node);
        }
    }

    private static String buildWaypointName(NodeData node) {
        StringBuilder sb = new StringBuilder();
        if (node.modifier != null && !node.modifier.isEmpty()) {
            sb.append(capitalize(node.modifier))
                .append(" ");
        }
        sb.append(capitalize(node.type))
            .append(" Node");

        int totalVis = node.getTotalVis();
        if (totalVis > 0) {
            sb.append(" (")
                .append(totalVis)
                .append(" Vis)");
        }
        return sb.toString();
    }

    public static Color getNodeColor(NodeData node) {
        if (node == null) return Color.WHITE;

        String type = node.type != null ? node.type.toUpperCase() : "NORMAL";
        switch (type) {
            case "PURE":
                return new Color(0, 230, 255); // Vibrant Cyan
            case "HUNGRY":
                return new Color(255, 45, 45); // Danger Red
            case "TAINTED":
                return new Color(210, 50, 230); // Taint Magenta
            case "DARK":
                return new Color(130, 40, 180); // Dark Nether Purple
            case "UNSTABLE":
                return new Color(255, 215, 0); // Electric Gold
            default:
                break;
        }

        String mod = node.modifier != null ? node.modifier.toUpperCase() : "";
        if ("BRIGHT".equals(mod)) {
            return new Color(80, 255, 120); // Bright Emerald Green
        } else if ("PALE".equals(mod)) {
            return new Color(170, 170, 170); // Pale Slate
        } else if ("FADING".equals(mod)) {
            return new Color(120, 120, 120); // Fading Charcoal
        }

        // Default: find highest aspect
        String topAspect = getDominantAspect(node.aspects);
        if (topAspect != null) {
            switch (topAspect.toLowerCase()) {
                case "aer":
                    return new Color(255, 255, 120);
                case "ignis":
                    return new Color(255, 90, 30);
                case "aqua":
                    return new Color(50, 150, 255);
                case "terra":
                    return new Color(80, 200, 60);
                case "ordo":
                    return new Color(240, 240, 255);
                case "perditio":
                    return new Color(70, 70, 80);
            }
        }

        return new Color(180, 140, 255); // Default Thaumic Violet
    }

    private static String getDominantAspect(Map<String, Integer> aspects) {
        if (aspects == null || aspects.isEmpty()) return null;
        String top = null;
        int max = -1;
        for (Map.Entry<String, Integer> entry : aspects.entrySet()) {
            if (entry.getValue() != null && entry.getValue() > max) {
                max = entry.getValue();
                top = entry.getKey();
            }
        }
        return top;
    }

    private static String capitalize(String str) {
        if (str == null || str.isEmpty()) return "";
        return str.substring(0, 1)
            .toUpperCase()
            + str.substring(1)
                .toLowerCase();
    }
}
