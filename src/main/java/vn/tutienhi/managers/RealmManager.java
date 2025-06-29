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
import vn.tutienhi.utils.ChatUtil;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class RealmManager {

    // Lớp Realm giữ nguyên, không cần thay đổi
    public static class Realm {
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
        
        // Sửa lại cách lấy dữ liệu từ config để an toàn hơn
        ConfigurationSection realmsSection = config.getConfigurationSection("realms");
        if (realmsSection == null) {
             plugin.getLogger().warning("Khong tim thay muc 'realms' trong realms.yml!");
             return;
        }

        for (String key : realmsSection.getKeys(false)) {
            // Thay vì dùng getMapList, chúng ta sẽ duyệt qua từng section
            ConfigurationSection realmMap = realmsSection.getConfigurationSection(key);
            if (realmMap == null) continue;

            try {
                // Lấy các giá trị trực tiếp từ section
                String id = realmMap.getString("id");
                String displayName = realmMap.getString("display-name");
                double maxLinhKhi = realmMap.getDouble("max-linh-khi");
                double linhKhiPerTick = realmMap.getDouble("linh-khi-per-tick");
                double lightningDamage = realmMap.getDouble("lightning-damage", 0.0);
                
                // Sửa lỗi đọc List<String>
                List<String> effects = realmMap.getStringList("permanent-effects");

                double bonusHealth = realmMap.getDouble("bonus-health", 0.0);
                double bonusDamage = realmMap.getDouble("bonus-damage", 0.0);
                
                Realm realm = new Realm(id, displayName, maxLinhKhi, linhKhiPerTick, lightningDamage, effects, bonusHealth, bonusDamage);
                
                realmsById.put(id, realm);
                realmOrder.add(id);
            } catch (Exception e) {
                plugin.getLogger().warning("Loi khi tai mot canh gioi tu realms.yml! Key: " + key);
                e.printStackTrace();
            }
        }
        plugin.getLogger().info("Da tai " + realmsById.size() + " canh gioi.");
    }
    
    // Các hàm còn lại giữ nguyên
    public void applyRealmBonuses(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;
        Realm realm = getRealm(data.getRealmId());
        if (realm == null) return;

        player.getActivePotionEffects().stream()
                .filter(effect -> effect.getDuration() > 20 * 60 * 10) 
                .map(PotionEffect::getType)
                .forEach(player::removePotionEffect);

        for (String effectString : realm.getPermanentEffects()) {
            try {
                String[] parts = effectString.split(":");
                PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
                int amplifier = Integer.parseInt(parts[1]);
                if (type != null) {
                    player.addPotionEffect(new PotionEffect(type, Integer.MAX_VALUE, amplifier, true, false));
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Hieu ung khong hop le trong realms.yml: " + effectString);
            }
        }
    }
    
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