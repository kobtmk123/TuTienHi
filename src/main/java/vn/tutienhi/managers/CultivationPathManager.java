package vn.tutienhi.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CultivationPathManager {

    public static class Path {
        private final String id;
        private final String displayName;
        private final double bonusHealth;
        private final double damageMultiplier;
        private final double swordDamageMultiplier;

        public Path(String id, String displayName, double bonusHealth, double damageMultiplier, double swordDamageMultiplier) {
            this.id = id;
            this.displayName = displayName;
            this.bonusHealth = bonusHealth;
            this.damageMultiplier = damageMultiplier;
            this.swordDamageMultiplier = swordDamageMultiplier;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public double getBonusHealth() { return bonusHealth; }
        public double getDamageMultiplier() { return damageMultiplier; }
        public double getSwordDamageMultiplier() { return swordDamageMultiplier; }
    }

    private final TuTienHi plugin;
    private final Map<String, Path> paths = new HashMap<>();

    public CultivationPathManager(TuTienHi plugin) {
        this.plugin = plugin;
        loadPaths();
    }

    public void loadPaths() {
        paths.clear();
        File pathsFile = new File(plugin.getDataFolder(), "cultivation_paths.yml");
        if (!pathsFile.exists()) {
            plugin.saveResource("cultivation_paths.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(pathsFile);
        ConfigurationSection pathSection = config.getConfigurationSection("paths");
        if (pathSection == null) return;

        for (String id : pathSection.getKeys(false)) {
            ConfigurationSection section = pathSection.getConfigurationSection(id);
            if (section != null) {
                String name = section.getString("display-name");
                double health = section.getDouble("bonus-health");
                double damage = section.getDouble("damage-multiplier");
                double swordDamage = section.getDouble("sword-damage-multiplier");
                paths.put(id, new Path(id, name, health, damage, swordDamage));
            }
        }
        // Thêm một "path" mặc định cho người chưa chọn
        paths.put("none", new Path("none", "&7Chua Chon", 0, 1.0, 1.0));
        plugin.getLogger().info("Da tai " + (paths.size() - 1) + " con duong tu tap.");
    }
    
    public Path getPath(String id) {
        return paths.getOrDefault(id, paths.get("none"));
    }

    public void applyPathBonus(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;
        
        // Lấy bonus từ Cảnh Giới và Con Đường
        RealmManager.Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        Path path = getPath(data.getCultivationPathId());
        
        if (realm == null || path == null) return;
        
        double totalBonusHealth = realm.getBonusHealth() + path.getBonusHealth();

        // Xóa các modifier cũ để tránh cộng dồn
        if (player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().stream()
            .anyMatch(m -> m.getName().equals("TuTienHiBonus"))) {
            AttributeModifier oldModifier = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().stream()
                .filter(m -> m.getName().equals("TuTienHiBonus")).findFirst().orElse(null);
            if(oldModifier != null) {
                 player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(oldModifier);
            }
        }

        // Áp dụng modifier mới
        if (totalBonusHealth > 0) {
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "TuTienHiBonus", totalBonusHealth, AttributeModifier.Operation.ADD_NUMBER);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(modifier);
        }
    }
}