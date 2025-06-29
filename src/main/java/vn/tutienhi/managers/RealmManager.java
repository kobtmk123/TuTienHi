package vn.tutienhi.managers;

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
    // ... (Không còn lớp Realm nội bộ ở đây nữa)

    private final TuTienHi plugin;
    private final Map<String, Realm> realmsById = new LinkedHashMap<>();
    private final List<String> realmOrder = new ArrayList<>();

    public RealmManager(TuTienHi plugin) {
        this.plugin = plugin;
        loadRealms();
    }

    public void loadRealms() {
        // ... (Giữ nguyên logic loadRealms từ phiên bản sửa lỗi cuối cùng)
    }

    public void applyRealmBonuses(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;
        Realm realm = getRealm(data.getRealmId()); // Sử dụng Realm độc lập
        if (realm == null) return;
        // ...
    }

    public Realm getRealm(String id) { return realmsById.get(id); }
    public Realm getInitialRealm() {
        if (realmOrder.isEmpty()) return null;
        return getRealm(realmOrder.get(0));
    }
    // ... các hàm khác
}