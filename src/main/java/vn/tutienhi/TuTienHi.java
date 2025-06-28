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

        // =================================================================
        // == ĐOẠN CODE TÍCH HỢP PLACEHOLDERAPI ĐÃ ĐƯỢC THÊM SẴN VÀO ĐÂY ==
        // =================================================================
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            // Nếu tìm thấy plugin PlaceholderAPI, đăng ký expansion
            new vn.tutienhi.papi.TuTienHiExpansion(this).register();
            getLogger().info("Da ket noi thanh cong voi PlaceholderAPI!");
        } else {
            // Nếu không tìm thấy, cảnh báo trong console
            getLogger().warning("Khong tim thay PlaceholderAPI, cac placeholder se khong hoat dong.");
        }
        // =================================================================

        getLogger().info(ChatUtil.colorize("&aPlugin TuTienHi da duoc bat!"));
    }

    @Override
    public void onDisable() {
        // Stop cultivation for all players to prevent issues
        if (cultivationTask != null) {
            cultivationTask.stopAllCultivation();
            cultivationTask.cancel();
        }

        // Save all player data
        if (playerDataManager != null) {
            playerDataManager.saveAllPlayerData();
        }

        getLogger().info(ChatUtil.colorize("&cPlugin TuTienHi da duoc tat."));
    }

    public void loadConfigs() {
        saveDefaultConfig();
        saveResourceIfNotExists("realms.yml");
        saveResourceIfNotExists("zones.yml");
    }
    
    public void reloadPluginConfigs() {
        reloadConfig();
        getRealmManager().loadRealms();
        getZoneManager().loadZones();
    }
    
    private void saveResourceIfNotExists(String resourcePath) {
        File file = new File(getDataFolder(), resourcePath);
        if (!file.exists()) {
            saveResource(resourcePath, false);
        }
    }

    // --- Getters ---
    public static TuTienHi getInstance() { return instance; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public RealmManager getRealmManager() { return realmManager; }
    public ZoneManager getZoneManager() { return zoneManager; }
    public CultivationTask getCultivationTask() { return cultivationTask; }
}