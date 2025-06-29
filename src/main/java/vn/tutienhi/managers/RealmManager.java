package vn.tutienhi.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.utils.ChatUtil;
import java.io.File;
import java.util.*;

public class RealmManager {

    public static class Realm {
        // ... (Constructor và Getters)
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
                
                double lightningDamage = ((Number) realmMap.getOrDefault("lightning-damage", 0.0)).doubleValue();
                List<String> effects = (List<String>) realmMap.getOrDefault("permanent-effects", Collections.emptyList());
                double bonusHealth = ((Number) realmMap.getOrDefault("bonus-health", 0.0)).doubleValue();
                double bonusDamage = ((Number) realmMap.getOrDefault("bonus-damage", 0.0)).doubleValue();

                Realm realm = new Realm(id, displayName, maxLinhKhi, linhKhiPerTick, lightningDamage, effects, bonusHealth, bonusDamage);
                
                realmsById.put(id, realm);
                realmOrder.add(id);
            } catch (Exception e) {
                plugin.getLogger().warning("Loi khi tai mot canh gioi tu realms.yml! ID: " + realmMap.get("id"));
            }
        }
        plugin.getLogger().info("Da tai " + realmsById.size() + " canh gioi.");
    }
    
    // ... các hàm khác
}