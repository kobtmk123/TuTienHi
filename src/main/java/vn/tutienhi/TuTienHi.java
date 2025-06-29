package vn.tutienhi;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import vn.tutienhi.commands.*;
import vn.tutienhi.data.PlayerDataManager;
import vn.tutienhi.gui.ShopGUI;
import vn.tutienhi.listeners.ItemListener;
import vn.tutienhi.listeners.PlayerListener;
import vn.tutienhi.managers.*;
import vn.tutienhi.tasks.CultivationTask;
import vn.tutienhi.utils.ChatUtil;
import java.io.File;

public final class TuTienHi extends JavaPlugin {

    private static TuTienHi instance;
    private PlayerDataManager playerDataManager;
    private RealmManager realmManager;
    private ZoneManager zoneManager;
    private CultivationTask cultivationTask;
    private ItemManager itemManager;
    private CultivationPathManager cultivationPathManager;
    
    private Economy economy = null;
    private NamespacedKey namespacedKey;

    @Override
    public void onEnable() {
        instance = this;
        this.namespacedKey = new NamespacedKey(this, "tutienhi_item_id");
        loadAllConfigs();
        if (!setupEconomy()) {
            getLogger().severe("Khong tim thay plugin Vault hoac mot plugin kinh te! Shop se khong hoat dong.");
        }
        this.itemManager = new ItemManager(this);
        this.cultivationPathManager = new CultivationPathManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.realmManager = new RealmManager(this);
        this.zoneManager = new ZoneManager(this);
        
        // Đăng ký Commands
        getCommand("tuluyen").setExecutor(new TuLuyenCommand(this));
        getCommand("dotpha").setExecutor(new DotPhaCommand(this));
        getCommand("tutienhi").setExecutor(new AdminCommand(this));
        getCommand("shoptiengioi").setExecutor(new ShopCommand(this));
        CultivationPathCommand pathCommand = new CultivationPathCommand(this);
        getCommand("conduongtutap").setExecutor(pathCommand);
        getCommand("kiemtu").setExecutor(pathCommand);
        getCommand("phattu").setExecutor(pathCommand);
        getCommand("matu").setExecutor(pathCommand);

        // Đăng ký Listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemListener(this), this);
        new ShopGUI(this); // Khởi tạo GUI để nó tự đăng ký listener

        // Chạy Tasks
        long tickRate = getConfig().getLong("settings.cultivation-tick-rate", 20L);
        this.cultivationTask = new CultivationTask(this);
        this.cultivationTask.runTaskTimer(this, 0L, tickRate);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new vn.tutienhi.papi.TuTienHiExpansion(this).register();
            getLogger().info("Da ket noi thanh cong voi PlaceholderAPI!");
        }
        getLogger().info(ChatUtil.colorize("&aPlugin TuTienHi v2.0 da duoc bat!"));
    }
    
    // ... các hàm khác giữ nguyên ...

    // SỬA LỖI: Thêm lại getter này
    public CultivationTask getCultivationTask() { 
        return cultivationTask; 
    }
    
    public static TuTienHi getInstance() { return instance; }
    public Economy getEconomy() { return economy; }
    public NamespacedKey getNamespacedKey() { return namespacedKey; }
    public PlayerDataManager getPlayerDataManager() { return playerDataManager; }
    public RealmManager getRealmManager() { return realmManager; }
    public ZoneManager getZoneManager() { return zoneManager; }
    public ItemManager getItemManager() { return itemManager; }
    public CultivationPathManager getCultivationPathManager() { return cultivationPathManager; }
}