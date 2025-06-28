package vn.tutienhi.managers;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vn.tutienhi.TuTienHi;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ZoneManager {

    public static class CultivationZone {
        private final String world;
        private final int minX, minY, minZ;
        private final int maxX, maxY, maxZ;
        private final double multiplier;

        public CultivationZone(String world, String pos1, String pos2, double multiplier) {
            this.world = world;
            this.multiplier = multiplier;

            String[] p1 = pos1.split(",");
            String[] p2 = pos2.split(",");

            int x1 = Integer.parseInt(p1[0]); int y1 = Integer.parseInt(p1[1]); int z1 = Integer.parseInt(p1[2]);
            int x2 = Integer.parseInt(p2[0]); int y2 = Integer.parseInt(p2[1]); int z2 = Integer.parseInt(p2[2]);

            this.minX = Math.min(x1, x2); this.minY = Math.min(y1, y2); this.minZ = Math.min(z1, z2);
            this.maxX = Math.max(x1, x2); this.maxY = Math.max(y1, y2); this.maxZ = Math.max(z1, z2);
        }
        
        public boolean isInZone(Location loc) {
            if (!loc.getWorld().getName().equalsIgnoreCase(this.world)) return false;
            return loc.getBlockX() >= minX && loc.getBlockX() <= maxX &&
                   loc.getBlockY() >= minY && loc.getBlockY() <= maxY &&
                   loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ;
        }

        public double getMultiplier() { return multiplier; }
    }

    private final TuTienHi plugin;
    private final List<CultivationZone> zones = new ArrayList<>();

    public ZoneManager(TuTienHi plugin) {
        this.plugin = plugin;
        loadZones();
    }

    public void loadZones() {
        zones.clear();
        File zonesFile = new File(plugin.getDataFolder(), "zones.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(zonesFile);

        List<Map<?, ?>> zoneList = config.getMapList("zones");
        for (Map<?, ?> zoneMap : zoneList) {
             try {
                String world = (String) zoneMap.get("world");
                String pos1 = (String) zoneMap.get("pos1");
                String pos2 = (String) zoneMap.get("pos2");
                double multiplier = ((Number) zoneMap.get("multiplier")).doubleValue();
                zones.add(new CultivationZone(world, pos1, pos2, multiplier));
             } catch (Exception e) {
                 plugin.getLogger().warning("Loi khi tai mot khu vuc tu zones.yml! Vui long kiem tra dinh dang.");
                 e.printStackTrace();
             }
        }
        plugin.getLogger().info("Da tai " + zones.size() + " khu vuc linh khi.");
    }

    public double getMultiplierAt(Location location) {
        for (CultivationZone zone : zones) {
            if (zone.isInZone(location)) {
                return zone.getMultiplier();
            }
        }
        return 1.0; // Default multiplier
    }
}