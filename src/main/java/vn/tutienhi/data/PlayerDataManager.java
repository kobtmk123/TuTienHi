package vn.tutienhi.data;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.managers.RealmManager;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final TuTienHi plugin;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private final File dataFolder;

    public PlayerDataManager(TuTienHi plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public PlayerData getPlayerData(Player player) {
        return playerDataMap.get(player.getUniqueId());
    }

    public void loadPlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        File playerFile = new File(dataFolder, uuid + ".yml");
        
        RealmManager.Realm initialRealm = plugin.getRealmManager().getInitialRealm();
        if (initialRealm == null) {
            plugin.getLogger().severe("KHONG THE TAI CANH GIOI KHOI DAU! Vui long kiem tra file realms.yml.");
            return;
        }

        if (!playerFile.exists()) {
            PlayerData newData = new PlayerData(initialRealm.getId(), 0);
            playerDataMap.put(uuid, newData);
            savePlayerData(player);
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
        String realmId = config.getString("realm", initialRealm.getId());
        double linhKhi = config.getDouble("linh-khi", 0);
        playerDataMap.put(uuid, new PlayerData(realmId, linhKhi));
    }

    public void savePlayerData(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) return;

        File playerFile = new File(dataFolder, uuid + ".yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

        config.set("name", player.getName());
        config.set("realm", data.getRealmId());
        config.set("linh-khi", data.getLinhKhi());

        try {
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Khong the luu du lieu cho nguoi choi " + player.getName());
            e.printStackTrace();
        }
    }
    
    public void unloadPlayerData(Player player) {
        savePlayerData(player);
        playerDataMap.remove(player.getUniqueId());
    }
    
    public void saveAllPlayerData() {
        plugin.getServer().getOnlinePlayers().forEach(this::savePlayerData);
    }
}