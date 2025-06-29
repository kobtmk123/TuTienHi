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

    // ... các biến instance giữ nguyên
    
    @Override
    public void onEnable() {
        instance = this;
        this.namespacedKey = new NamespacedKey(this, "tutienhi_item_id");
        
        // SỬA LỖI: Gọi đúng tên hàm loadAllConfigs()
        loadAllConfigs(); 
        
        if (!setupEconomy()) {
            getLogger().severe("Khong tim thay plugin Vault hoac mot plugin kinh te! Shop se khong hoat dong.");
        }
        
        this.itemManager = new ItemManager(this);
        this.cultivationPathManager = new CultivationPathManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.realmManager = new RealmManager(this);
        this.zoneManager = new ZoneManager(this);
        
        // ... đăng ký command và listener giữ nguyên ...

        new ShopGUI(this);
        
        long tickRate = getConfig().getLong("settings.cultivation-tick-rate", 20L);
        this.cultivationTask = new CultivationTask(this);
        this.cultivationTask.runTaskTimer(this, 0L, tickRate);

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new vn.tutienhi.papi.TuTienHiExpansion(this).register();
            getLogger().info("Da ket noi thanh cong voi PlaceholderAPI!");
        }
        getLogger().info(ChatUtil.colorize("&aPlugin TuTienHi v2.0 da duoc bat!"));
    }

    // ... onDisable() giữ nguyên ...

    // SỬA LỖI: Các hàm này phải tồn tại
    public void loadAllConfigs() {
        saveDefaultConfig();
        saveResourceIfNotExists("realms.yml");
        saveResourceIfNotExists("zones.yml");
        saveResourceIfNotExists("items.yml");
        saveResourceIfNotExists("shop.yml");
        saveResourceIfNotExists("cultivation_paths.yml");
    }
    
    public void reloadAllConfigs() {
        reloadConfig();
        getRealmManager().loadRealms();
        getZoneManager().loadZones();
        getItemManager().loadItems();
        getCultivationPathManager().loadPaths();
    }
    
    private void saveResourceIfNotExists(String resourcePath) { /* ... */ }
    
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    // ... các getters giữ nguyên, bao gồm cả getCultivationTask()
}