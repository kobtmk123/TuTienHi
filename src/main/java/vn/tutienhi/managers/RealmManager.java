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
import vn.tutienhi.models.Realm; // Import lớp Realm mới
import java.io.File;
import java.util.*;

public class RealmManager {
    // Không còn lớp Realm nội bộ ở đây nữa

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

    // Các hàm trả về đều dùng Realm độc lập
    public Realm getRealm(String id) { return realmsById.get(id); }
    public Realm getInitialRealm() {
        if (realmOrder.isEmpty()) return null;
        return getRealm(realmOrder.get(0));
    }
    public Realm getNextRealm(String currentRealmId) {
        // SỬA LỖI: Thêm khai báo biến currentIndex
        int currentIndex = realmOrder.indexOf(currentRealmId);
        if (currentIndex == -1 || currentIndex + 1 >= realmOrder.size()) return null;
        return getRealm(realmOrder.get(currentIndex + 1));
    }
    public List<Realm> getRealms() {
        return new ArrayList<>(realmsById.values());
    }
    public List<String> getRealmOrder() { return realmOrder; }
    public int getTotalRealms() { return realmOrder.size(); }
}