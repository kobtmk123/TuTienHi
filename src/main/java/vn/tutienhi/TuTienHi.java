package vn.tutienhi;

import org.bukkit.plugin.java.JavaPlugin;
import vn.tutienhi.commands.AdminCommand;
import vn.tutienhi.commands.DotPhaCommand;
import vn.tutienhi.commands.TuLuyenCommand;
import vn.tutienhi.data.PlayerDataManager;
import vn.tutienhi.listeners.PlayerListener;
import vn.tutienhi.managers.RealmManager;
import vn.tutienhi.managers.ZoneManager;
import vn.tutienhi.tasks.CultivationTask;
import vn.tutienhi.utils.ChatUtil;

import java.io.File;

public final class TuTienHi extends JavaPlugin {

    private static TuTienHi instance;
    private PlayerDataManager playerDataManager;
    private RealmManager realmManager;
    private ZoneManager zoneManager;
    private CultivationTask cultivationTask;

    @Override
    public void onEnable() {
        instance = this;

        // Load configurations
        loadConfigs();

        // Initialize managers
        this.playerDataManager = new PlayerDataManager(this);
        this.realmManager = new RealmManager(this);
        this.zoneManager = new ZoneManager(this);

        // Register commands
        getCommand("tuluyen").setExecutor(new TuLuyenCommand(this));
        getCommand("dotpha").setExecutor(new DotPhaCommand(this));
        getCommand("tutienhi").setExecutor(new AdminCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // Start tasks
        long tickRate = getConfig().getLong("settings.cultivation-tick-rate", 20L);
        this.cultivationTask = new CultivationTask(this);
        this.cultivationTask.runTaskTimer(this, 0L, tickRate);

        getLogger().info(ChatUtil.colorize("&aPlugin TuTienHi đã được bật!"));
    }

    @Override
    public void onDisable() {
        // Stop cultivation for all players to prevent issues
        if (cultivationTask != null) {
            cultivationTask.stopAllCultivation();
            cultivationTask.cancel();
        }

        // Save all player data
        playerDataManager.saveAllPlayerData();

        getLogger().info(ChatUtil.colorize("&cPlugin TuTienHi đã được tắt."));
    }

    public void loadConfigs() {
        // Create default files if they don't exist
        saveDefaultConfig();
        
        File realmsFile = new File(getDataFolder(), "realms.yml");
        if (!realmsFile.exists()) {
            saveResource("realms.yml", false);
        }

        File zonesFile = new File(getDataFolder(), "zones.yml");
        if (!zonesFile.exists()) {
            saveResource("zones.yml", false);
        }
    }
    
    public void reloadPluginConfigs() {
        reloadConfig();
        // Reload custom configs
        getRealmManager().loadRealms();
        getZoneManager().loadZones();
    }

    public static TuTienHi getInstance() {
        return instance;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public RealmManager getRealmManager() {
        return realmManager;
    }
    
    public ZoneManager getZoneManager() {
        return zoneManager;
    }

    public CultivationTask getCultivationTask() {
        return cultivationTask;
    }
}