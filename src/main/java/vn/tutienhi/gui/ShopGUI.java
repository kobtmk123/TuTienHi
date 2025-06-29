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
        // Lấy tiêu đề một lần khi khởi tạo để so sánh
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

                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(slotStr);
                if (itemConfig == null) continue;
                
                String customItemId = itemConfig.getString("item_id");
                ItemStack displayItem;

                if (customItemId != null) {
                    displayItem = plugin.getItemManager().getItem(customItemId);
                    if(displayItem == null) {
                        plugin.getLogger().warning("Shop item tai slot " + slot + " co item_id khong ton tai: " + customItemId);
                        continue;
                    }
                } else {
                    Material material = Material.valueOf(itemConfig.getString("material", "STONE").toUpperCase());
                    displayItem = new ItemStack(material);
                }

                ItemMeta meta = displayItem.getItemMeta();
                if (meta == null) continue;

                if (itemConfig.contains("display-name")) {
                    meta.setDisplayName(ChatUtil.colorize(itemConfig.getString("display-name")));
                }
                
                List<String> newLore = new ArrayList<>(meta.hasLore() ? meta.getLore() : new ArrayList<>());
                double price = itemConfig.getDouble("price", 0.0);
                for (String loreLine : itemConfig.getStringList("lore")) {
                    newLore.add(ChatUtil.colorize(loreLine.replace("%price%", String.format("%,.2f", price))));
                }
                meta.setLore(newLore);
                
                displayItem.setItemMeta(meta);
                shopInventory.setItem(slot, displayItem);

            } catch (Exception e) {
                plugin.getLogger().warning("Loi khi tai vat pham trong shop o slot: " + slotStr);
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
            
            if(itemConfig.getBoolean("close_on_click", false)) {
                player.closeInventory();
                return;
            }

            double price = itemConfig.getDouble("price");
            String itemId = itemConfig.getString("item_id");
            if (price <= 0 || itemId == null) return;

            Economy econ = plugin.getEconomy();
            if (econ == null) {
                player.sendMessage(ChatUtil.colorize("&cHe thong kinh te khong hoat dong."));
                return;
            }
            
            if (econ.getBalance(player) < price) {
                String message = plugin.getConfig().getString("messages.not-enough-money")
                    .replace("%cost%", String.format("%,.2f", price))
                    .replace("%balance%", String.format("%,.2f", econ.getBalance(player)));
                player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));
                return;
            }
            
            EconomyResponse r = econ.withdrawPlayer(player, price);
            if (r.transactionSuccess()) {
                ItemStack itemToGive = plugin.getItemManager().getItem(itemId);
                if (itemToGive != null) {
                    player.getInventory().addItem(itemToGive);
                    String message = plugin.getConfig().getString("messages.purchase-success")
                        .replace("%item_name%", clickedItem.getItemMeta().getDisplayName())
                        .replace("%cost%", String.format("%,.2f", price));
                    player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));
                }
            } else {
                player.sendMessage(ChatUtil.colorize("&cLoi giao dich: " + r.errorMessage));
            }
        }
    }
}