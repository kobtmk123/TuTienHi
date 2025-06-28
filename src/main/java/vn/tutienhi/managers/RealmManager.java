package vn.tutienhi.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.utils.ChatUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RealmManager {

    public static class Realm {
        private final String id;
        private final String displayName;
        private final double maxLinhKhi;
        private final double linhKhiPerTick;

        public Realm(String id, String displayName, double maxLinhKhi, double linhKhiPerTick) {
            this.id = id;
            this.displayName = ChatUtil.colorize(displayName);
            this.maxLinhKhi = maxLinhKhi;
            this.linhKhiPerTick = linhKhiPerTick;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public double getMaxLinhKhi() { return maxLinhKhi; }
        public double getLinhKhiPerTick() { return linhKhiPerTick; }
    }

    private final TuTienHi plugin;
    private final Map<String, Realm> realmsById = new LinkedHashMap<>();
    private final List<String> realmOrder = new ArrayList<>();

    public RealmManager(TuTienHi plugin) {
        this.plugin = plugin;
        loadRealms();
    }

    public void loadRealms() {
        realmsById.clear();
        realmOrder.clear();
        File realmsFile = new File(plugin.getDataFolder(), "realms.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(realmsFile);

        List<Map<?, ?>> realmList = config.getMapList("realms");
        for (Map<?, ?> realmMap : realmList) {
            try {
                String id = (String) realmMap.get("id");
                String displayName = (String) realmMap.get("display-name");
                double maxLinhKhi = ((Number) realmMap.get("max-linh-khi")).doubleValue();
                double linhKhiPerTick = ((Number) realmMap.get("linh-khi-per-tick")).doubleValue();
            
                Realm realm = new Realm(id, displayName, maxLinhKhi, linhKhiPerTick);
                realmsById.put(id, realm);
                realmOrder.add(id);
            } catch (Exception e) {
                plugin.getLogger().warning("Loi khi tai mot canh gioi tu realms.yml! Vui long kiem tra dinh dang.");
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Da tai " + realmsById.size() + " canh gioi.");
    }
    
    public Realm getRealm(String id) {
        return realmsById.get(id);
    }

    public Realm getInitialRealm() {
        if (realmOrder.isEmpty()) return null;
        return getRealm(realmOrder.get(0));
    }
    
    public Realm getNextRealm(String currentRealmId) {
        int currentIndex = realmOrder.indexOf(currentRealmId);
        if (currentIndex == -1 || currentIndex + 1 >= realmOrder.size()) {
            return null; // No next realm or current realm not found
        }
        return getRealm(realmOrder.get(currentIndex + 1));
    }
}