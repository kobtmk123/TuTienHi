package vn.tutienhi.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
// ... các import khác ...
import java.io.File;
import java.util.*;

public class RealmManager {

    // ... (lớp Realm giữ nguyên) ...

    public void loadRealms() {
        realmsById.clear();
        realmOrder.clear();
        File realmsFile = new File(plugin.getDataFolder(), "realms.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(realmsFile);
        
        List<Map<?, ?>> realmList = config.getMapList("realms");

        for (Map<?, ?> realmMap : realmList) {
            try {
                // ... (code parse giữ nguyên) ...
                
                // Sửa lỗi ép kiểu List<String>
                List<String> effects = (List<String>) realmMap.getOrDefault("permanent-effects", Collections.emptyList());

                Realm realm = new Realm(id, displayName, maxLinhKhi, linhKhiPerTick, lightningDamage, effects, bonusHealth, bonusDamage);
                
                realmsById.put(id, realm);
                realmOrder.add(id);
            } catch (Exception e) {
                // ...
            }
        }
        plugin.getLogger().info("Da tai " + realmsById.size() + " canh gioi.");
    }
    
    // ... các hàm khác ...
}