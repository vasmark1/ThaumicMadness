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
        if (node == null) return new Color(171, 71, 188); // Thaumic Purple

        if (node.type != null) {
            String type = node.type.trim()
                .toUpperCase();
            if (type.contains("PURE")) {
                return new Color(0, 229, 255); // Vibrant Cyan
            } else if (type.contains("HUNGRY")) {
                return new Color(255, 23, 68); // Neon Red
            } else if (type.contains("TAINT")) {
                return new Color(213, 0, 249); // Taint Flux Magenta
            } else if (type.contains("DARK") || type.contains("OMINOUS") || type.contains("SINISTER")) {
                return new Color(74, 20, 140); // Deep Dark Indigo Purple
            } else if (type.contains("UNSTABLE")) {
                return new Color(255, 214, 0); // Electric Gold
            }
        }

        // For NORMAL nodes, color by dominant primal aspect
        String topAspect = getDominantAspect(node.aspects);
        if (topAspect != null) {
            switch (topAspect.toLowerCase()) {
                case "aer":
                    return new Color(255, 234, 0); // Bright Yellow
                case "ignis":
                    return new Color(255, 61, 0); // Fiery Red-Orange
                case "aqua":
                    return new Color(0, 176, 255); // Sky Blue
                case "terra":
                    return new Color(0, 230, 118); // Spring Green
                case "ordo":
                    return new Color(236, 239, 241); // Pure White/Silver
                case "perditio":
                    return new Color(66, 66, 66); // Dark Charcoal
            }
        }

        return new Color(171, 71, 188); // Classic Thaumcraft Arcane Purple
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
