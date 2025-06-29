package vn.tutienhi.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.models.Realm; // SỬA LỖI: Thêm import này
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CultivationPathManager {

    public static class Path {
        // ... (Nội dung lớp Path giữ nguyên)
    }

    private final TuTienHi plugin;
    private final Map<String, Path> paths = new HashMap<>();

    public CultivationPathManager(TuTienHi plugin) {
        this.plugin = plugin;
        loadPaths();
    }

    public void loadPaths() {
        // ... (Nội dung loadPaths giữ nguyên)
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