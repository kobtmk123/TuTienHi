package vn.tutienhi.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
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
        // ... (Nội dung lớp Realm giữ nguyên, đảm bảo có constructor)
        private final String id;
        private final String displayName;
        private final double maxLinhKhi;
        private final double linhKhiPerTick;
        private final double lightningDamage;
        private final List<String> permanentEffects;
        private final double bonusHealth;
        private final double bonusDamage;

        public Realm(String id, String displayName, double maxLinhKhi, double linhKhiPerTick, double lightningDamage, List<String> permanentEffects, double bonusHealth, double bonusDamage) {
            this.id = id;
            this.displayName = ChatUtil.colorize(displayName);
            this.maxLinhKhi = maxLinhKhi;
            this.linhKhiPerTick = linhKhiPerTick;
            this.lightningDamage = lightningDamage;
            this.permanentEffects = permanentEffects;
            this.bonusHealth = bonusHealth;
            this.bonusDamage = bonusDamage;
        }

        // Getters
        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public double getMaxLinhKhi() { return maxLinhKhi; }
        public double getLinhKhiPerTick() { return linhKhiPerTick; }
        public double getLightningDamage() { return lightningDamage; }
        public List<String> getPermanentEffects() { return permanentEffects; }
        public double getBonusHealth() { return bonusHealth; }
        public double getBonusDamage() { return bonusDamage; }
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
        
        // Quay lại cách đọc đơn giản nhưng an toàn hơn
        List<Map<?, ?>> realmList = config.getMapList("realms");

        for (Map<?, ?> realmMap : realmList) {
            try {
                String id = (String) realmMap.get("id");
                String displayName = (String) realmMap.get("display-name");
                double maxLinhKhi = ((Number) realmMap.get("max-linh-khi")).doubleValue();
                double linhKhiPerTick = ((Number) realmMap.get("linh-khi-per-tick")).doubleValue();
                
                // SỬA LỖI: Dùng getOrDefault để tránh NullPointerException và ép kiểu
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
    
    // Các hàm còn lại giữ nguyên...
    public void applyRealmBonuses(Player player) { /*...*/ }
    public Realm getRealm(String id) { return realmsById.get(id); }
    public Realm getInitialRealm() { if (realmOrder.isEmpty()) return null; return getRealm(realmOrder.get(0)); }
    public Realm getNextRealm(String currentRealmId) {
        int currentIndex = realmOrder.indexOf(currentRealmId);
        if (currentIndex == -1 || currentIndex + 1 >= realmOrder.size()) return null;
        return getRealm(realmOrder.get(currentIndex + 1));
    }
    public List<String> getRealmOrder() { return realmOrder; }
    public int getTotalRealms() { return realmOrder.size(); }
}