package vn.tutienhi.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.models.Realm; // Thêm import này
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CultivationPathManager {

    public static class Path {
        // ... (Giữ nguyên lớp Path)
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
        // ... (Giữ nguyên)
    }
    
    public Path getPath(String id) {
        return paths.getOrDefault(id, paths.get("none"));
    }

    public void applyPathBonus(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;
        
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        Path path = getPath(data.getCultivationPathId());
        
        if (realm == null || path == null) return;
        
        double totalBonusHealth = realm.getBonusHealth() + path.getBonusHealth();

        // Xóa modifier cũ
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers().stream()
            .filter(m -> m.getName().equals("TuTienHiBonus"))
            .forEach(m -> player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(m));

        if (totalBonusHealth != 0) {
            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), "TuTienHiBonus", totalBonusHealth, AttributeModifier.Operation.ADD_NUMBER);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).addModifier(modifier);
        }
    }
}