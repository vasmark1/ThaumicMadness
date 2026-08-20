package com.vasmark.thaumicmadness.nodetracker;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;

public class NodeTrackerManager {

    private static final Logger LOGGER = LogManager.getLogger("ThaumicMadness-NodeTracker");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting()
        .create();
    private static final NodeTrackerManager INSTANCE = new NodeTrackerManager();

    private final List<NodeData> nodes = new ArrayList<NodeData>();
    private NodeData activeTarget = null;
    private String currentServerOrWorld = "local";

    public static NodeTrackerManager getInstance() {
        return INSTANCE;
    }

    public List<NodeData> getNodes() {
        return nodes;
    }

    public NodeData getActiveTarget() {
        return activeTarget;
    }

    public void setActiveTarget(NodeData target) {
        this.activeTarget = target;
    }

    public void clearActiveTarget() {
        this.activeTarget = null;
    }

    public void setSessionId(String sessionId) {
        if (sessionId == null || sessionId.trim()
            .isEmpty()) {
            this.currentServerOrWorld = "local";
        } else {
            this.currentServerOrWorld = sessionId.replaceAll("[^a-zA-Z0-9_.-]", "_");
        }
        loadFromJson();
    }

    public synchronized boolean addOrUpdateNode(INode node, int dim, int x, int y, int z) {
        if (node == null) return false;

        AspectList aspectList = node.getAspects();
        Map<String, Integer> aspects = new HashMap<String, Integer>();
        if (aspectList != null) {
            for (Aspect aspect : aspectList.getAspects()) {
                if (aspect != null) {
                    aspects.put(aspect.getTag(), aspectList.getAmount(aspect));
                }
            }
        }

        NodeType nodeType = node.getNodeType();
        String typeStr = nodeType != null ? nodeType.name() : "NORMAL";

        NodeModifier nodeMod = node.getNodeModifier();
        String modStr = nodeMod != null ? nodeMod.name() : null;

        NodeData targetNode = null;
        for (NodeData existing : nodes) {
            if (existing.x == x && existing.y == y && existing.z == z && existing.dim == dim) {
                targetNode = existing;
                break;
            }
        }

        if (targetNode != null) {
            targetNode.type = typeStr;
            targetNode.modifier = modStr;
            targetNode.aspects.clear();
            targetNode.aspects.putAll(aspects);
            targetNode.timestamp = System.currentTimeMillis();
        } else {
            targetNode = new NodeData(x, y, z, dim, typeStr, modStr, aspects);
            nodes.add(targetNode);
        }

        saveToJson();
        LOGGER.info("Tracked node at [dim:{}, x:{}, y:{}, z:{}] with {} aspects", dim, x, y, z, aspects.size());
        return true;
    }

    public synchronized void deleteNode(NodeData node) {
        if (node == null) return;
        nodes.remove(node);
        if (activeTarget != null && activeTarget.equals(node)) {
            activeTarget = null;
        }
        saveToJson();
    }

    public synchronized void clearAllNodes() {
        nodes.clear();
        activeTarget = null;
        saveToJson();
    }

    public String getDimensionName(int dimId) {
        try {
            WorldProvider provider = DimensionManager.createProviderFor(dimId);
            if (provider != null) {
                return provider.getDimensionName();
            }
        } catch (Throwable ignored) {}

        if (dimId == 0) return "Overworld";
        if (dimId == -1) return "Nether";
        if (dimId == 1) return "The End";
        return "Dimension " + dimId;
    }

    public List<Integer> getAvailableDimensions() {
        List<Integer> dims = new ArrayList<Integer>();
        for (NodeData node : nodes) {
            if (!dims.contains(node.dim)) {
                dims.add(node.dim);
            }
        }
        Collections.sort(dims);
        return dims;
    }

    public List<NodeData> getFilteredAndSortedNodes(String searchFilter, Integer dimFilter, String typeFilter,
        SortMode sortMode, double playerX, double playerY, double playerZ) {
        List<NodeData> result = new ArrayList<NodeData>();

        String filterLower = searchFilter != null ? searchFilter.trim()
            .toLowerCase() : "";

        for (NodeData node : nodes) {
            // Dimension filter
            if (dimFilter != null && node.dim != dimFilter.intValue()) {
                continue;
            }

            // Type filter
            if (typeFilter != null && !typeFilter.equalsIgnoreCase("ALL")) {
                if (!typeFilter.equalsIgnoreCase(node.type) && !typeFilter.equalsIgnoreCase(node.modifier)) {
                    continue;
                }
            }

            // Text search filter (checks aspect tags, aspect localized names, coordinates, type)
            if (!filterLower.isEmpty()) {
                boolean matches = false;

                // Check coords: "123", "-456"
                String coords = node.x + " " + node.y + " " + node.z;
                if (coords.contains(filterLower)) {
                    matches = true;
                }

                // Check type or modifier
                if (node.type != null && node.type.toLowerCase()
                    .contains(filterLower)) matches = true;
                if (node.modifier != null && node.modifier.toLowerCase()
                    .contains(filterLower)) matches = true;
                if (node.getFormattedType()
                    .toLowerCase()
                    .contains(filterLower)) matches = true;
                if (node.getFormattedModifier()
                    .toLowerCase()
                    .contains(filterLower)) matches = true;

                // Check aspect tags and localized names
                if (!matches) {
                    for (String aspectTag : node.aspects.keySet()) {
                        if (aspectTag.toLowerCase()
                            .contains(filterLower)) {
                            matches = true;
                            break;
                        }
                        Aspect asp = Aspect.getAspect(aspectTag);
                        if (asp != null && asp.getName()
                            .toLowerCase()
                            .contains(filterLower)) {
                            matches = true;
                            break;
                        }
                    }
                }

                if (!matches) {
                    continue;
                }
            }

            result.add(node);
        }

        // Sorting
        final double px = playerX;
        final double py = playerY;
        final double pz = playerZ;

        Comparator<NodeData> comparator = null;
        switch (sortMode) {
            case DISTANCE_ASC:
                comparator = new Comparator<NodeData>() {

                    @Override
                    public int compare(NodeData a, NodeData b) {
                        return Double.compare(a.getDistanceSq(px, py, pz), b.getDistanceSq(px, py, pz));
                    }
                };
                break;
            case DISTANCE_DESC:
                comparator = new Comparator<NodeData>() {

                    @Override
                    public int compare(NodeData a, NodeData b) {
                        return Double.compare(b.getDistanceSq(px, py, pz), a.getDistanceSq(px, py, pz));
                    }
                };
                break;
            case TOTAL_VIS_DESC:
                comparator = new Comparator<NodeData>() {

                    @Override
                    public int compare(NodeData a, NodeData b) {
                        return Integer.compare(b.getTotalVis(), a.getTotalVis());
                    }
                };
                break;
            case TOTAL_VIS_ASC:
                comparator = new Comparator<NodeData>() {

                    @Override
                    public int compare(NodeData a, NodeData b) {
                        return Integer.compare(a.getTotalVis(), b.getTotalVis());
                    }
                };
                break;
            case AER:
            case TERRA:
            case IGNIS:
            case AQUA:
            case ORDO:
            case PERDITIO:
                final String tag = sortMode.name()
                    .toLowerCase();
                comparator = new Comparator<NodeData>() {

                    @Override
                    public int compare(NodeData a, NodeData b) {
                        return Integer.compare(b.getAspectAmount(tag), a.getAspectAmount(tag));
                    }
                };
                break;
        }

        if (comparator != null) {
            Collections.sort(result, comparator);
        }

        return result;
    }

    private File getSaveFile() {
        File dir = new File(Minecraft.getMinecraft().mcDataDir, "nodetracker");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, currentServerOrWorld + ".json");
    }

    public synchronized void saveToJson() {
        try {
            File file = getSaveFile();
            FileWriter writer = new FileWriter(file);
            GSON.toJson(nodes, writer);
            writer.close();
        } catch (Throwable t) {
            LOGGER.error("Failed to save node tracker data: ", t);
        }
    }

    public synchronized void loadFromJson() {
        nodes.clear();
        activeTarget = null;
        try {
            File file = getSaveFile();
            if (file.exists()) {
                FileReader reader = new FileReader(file);
                Type type = new TypeToken<List<NodeData>>() {}.getType();
                List<NodeData> loaded = GSON.fromJson(reader, type);
                reader.close();
                if (loaded != null) {
                    nodes.addAll(loaded);
                }
                LOGGER.info("Loaded {} nodes from {}", nodes.size(), file.getName());
            }
        } catch (Throwable t) {
            LOGGER.error("Failed to load node tracker data: ", t);
        }
    }

    public enum SortMode {
        DISTANCE_ASC,
        DISTANCE_DESC,
        TOTAL_VIS_DESC,
        TOTAL_VIS_ASC,
        AER,
        TERRA,
        IGNIS,
        AQUA,
        ORDO,
        PERDITIO
    }
}
