package vn.tutienhi.gui;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.utils.ChatUtil;
import java.util.ArrayList;
import java.util.List;

public class ShopGUI implements Listener {

    private final TuTienHi plugin;
    private final String shopTitle;

    public ShopGUI(TuTienHi plugin) {
        this.plugin = plugin;
        this.shopTitle = ChatUtil.colorize(plugin.getShopConfig().getString("shop.title", "&8Shop"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        FileConfiguration shopConfig = plugin.getShopConfig();
        int size = shopConfig.getInt("shop.size", 27);
        Inventory shopInventory = Bukkit.createInventory(player, size, this.shopTitle);
        
        ConfigurationSection itemsSection = shopConfig.getConfigurationSection("shop.items");
        if (itemsSection == null) {
            player.openInventory(shopInventory);
            return;
        }

        for (String slotStr : itemsSection.getKeys(false)) {
            try {
                int slot = Integer.parseInt(slotStr);
                if (slot >= size) continue;
                // ... (code tạo item giữ nguyên)
            } catch (Exception e) {
                // ...
            }
        }
        player.openInventory(shopInventory);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        if (event.getView().getTitle().equals(this.shopTitle)) {
            event.setCancelled(true);
            
            Player player = (Player) event.getWhoClicked();
            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType().isAir()) return;
            
            String slotStr = String.valueOf(event.getSlot());
            ConfigurationSection itemConfig = plugin.getShopConfig().getConfigurationSection("shop.items." + slotStr);
            if (itemConfig == null) return;
            
            // ... (code xử lý mua bán giữ nguyên)
        }
    }
}