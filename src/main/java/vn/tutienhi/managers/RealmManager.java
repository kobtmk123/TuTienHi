package vn.tutienhi.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.models.Realm;
import java.io.File;
import java.util.*;

public class RealmManager {

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
        
        ConfigurationSection realmsSection = config.getConfigurationSection("realms");
        if (realmsSection == null) {
            plugin.getLogger().warning("Khong tim thay section 'realms' trong realms.yml!");
            return;
        }

        for (String key : realmsSection.getKeys(false)) {
            ConfigurationSection realmSection = realmsSection.getConfigurationSection(key);
            if (realmSection == null) continue;

            try {
                String id = realmSection.getString("id");
                if (id == null) {
                    plugin.getLogger().warning("Canh gioi voi key '" + key + "' thieu ID!");
                    continue;
                }
                
                String displayName = realmSection.getString("display-name");
                double maxLinhKhi = realmSection.getDouble("max-linh-khi");
                double linhKhiPerTick = realmSection.getDouble("linh-khi-per-tick");
                double lightningDamage = realmSection.getDouble("lightning-damage", 0.0);
                List<String> effects = realmSection.getStringList("permanent-effects");
                double bonusHealth = realmSection.getDouble("bonus-health", 0.0);
                double bonusDamage = realmSection.getDouble("bonus-damage", 0.0);

                Realm realm = new Realm(id, displayName, maxLinhKhi, linhKhiPerTick, lightningDamage, effects, bonusHealth, bonusDamage);
                
                realmsById.put(id, realm);
                realmOrder.add(id);
            } catch (Exception e) {
                plugin.getLogger().severe("Loi nghiem trong khi tai canh gioi voi key '" + key + "': " + e.getMessage());
            }
        }
        plugin.getLogger().info("Da tai " + realmsById.size() + " canh gioi.");
    }
    
    // Các hàm còn lại giữ nguyên
}