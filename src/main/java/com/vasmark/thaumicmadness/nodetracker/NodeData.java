package com.vasmark.thaumicmadness.nodetracker;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.StatCollector;

import thaumcraft.api.aspects.Aspect;

public class NodeData {

    public int x;
    public int y;
    public int z;
    public int dim;
    public String type; // NORMAL, UNSTABLE, DARK, TAINTED, HUNGRY, PURE
    public String modifier; // BRIGHT, PALE, FADING, or null
    public Map<String, Integer> aspects = new HashMap<String, Integer>();
    public long timestamp;

    public NodeData() {
        this.timestamp = System.currentTimeMillis();
    }

    public NodeData(int x, int y, int z, int dim, String type, String modifier, Map<String, Integer> aspects) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.type = type != null ? type : "NORMAL";
        this.modifier = modifier;
        if (aspects != null) {
            this.aspects.putAll(aspects);
        }
        this.timestamp = System.currentTimeMillis();
    }

    public int getTotalVis() {
        int total = 0;
        for (Integer val : aspects.values()) {
            if (val != null) {
                total += val;
            }
        }
        return total;
    }

    public int getAspectAmount(String aspectTag) {
        Integer val = aspects.get(aspectTag);
        return val != null ? val : 0;
    }

    public int getAspectAmount(Aspect aspect) {
        if (aspect == null) return 0;
        return getAspectAmount(aspect.getTag());
    }

    public double getDistanceSq(double px, double py, double pz) {
        double dx = this.x + 0.5 - px;
        double dy = this.y + 0.5 - py;
        double dz = this.z + 0.5 - pz;
        return dx * dx + dy * dy + dz * dz;
    }

    public double getDistance(double px, double py, double pz) {
        return Math.sqrt(getDistanceSq(px, py, pz));
    }

    public String getFormattedType() {
        if (type == null) return StatCollector.translateToLocal("nodetracker.type.normal");
        String key = "nodetracker.type." + type.toLowerCase();
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        return type;
    }

    public String getFormattedModifier() {
        if (modifier == null || modifier.isEmpty()) return "";
        String key = "nodetracker.mod." + modifier.toLowerCase();
        if (StatCollector.canTranslate(key)) {
            return StatCollector.translateToLocal(key);
        }
        return modifier;
    }

    public String getTypeColorCode() {
        if ("HUNGRY".equalsIgnoreCase(type)) return "§c";
        if ("DARK".equalsIgnoreCase(type)) return "§5";
        if ("TAINTED".equalsIgnoreCase(type)) return "§d";
        if ("PURE".equalsIgnoreCase(type)) return "§b";
        if ("UNSTABLE".equalsIgnoreCase(type)) return "§e";
        return "§f";
    }

    public String getModifierColorCode() {
        if ("BRIGHT".equalsIgnoreCase(modifier)) return "§a";
        if ("PALE".equalsIgnoreCase(modifier)) return "§8";
        if ("FADING".equalsIgnoreCase(modifier)) return "§4";
        return "§7";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        NodeData other = (NodeData) obj;
        return x == other.x && y == other.y && z == other.z && dim == other.dim;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        result = 31 * result + dim;
        return result;
    }
}
