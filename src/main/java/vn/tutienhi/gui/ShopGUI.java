package vn.tutienhi.gui;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.utils.ChatUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShopGUI implements Listener {

    private final TuTienHi plugin;
    private Inventory shopInventory;

    public ShopGUI(TuTienHi plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        createShop();
    }
    
    private void createShop() {
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(shopFile);

        String title = ChatUtil.colorize(config.getString("shop.title", "&8Shop"));
        int size = config.getInt("shop.size", 27);
        shopInventory = Bukkit.createInventory(null, size, title);
        
        ConfigurationSection itemsSection = config.getConfigurationSection("shop.items");
        if(itemsSection == null) return;

        for (String slotStr : itemsSection.getKeys(false)) {
            try {
                int slot = Integer.parseInt(slotStr);
                ConfigurationSection itemConfig = itemsSection.getConfigurationSection(slotStr);
                if (itemConfig == null) continue;
                
                ItemStack displayItem;
                String customItemId = itemConfig.getString("item_id");
                
                // Nếu có item_id, lấy từ ItemManager
                if (customItemId != null) {
                    displayItem = plugin.getItemManager().getItem(customItemId);
                    if(displayItem == null) continue;
                } else { // Nếu không, tạo vật phẩm trang trí
                    Material material = Material.valueOf(itemConfig.getString("material", "STONE").toUpperCase());
                    displayItem = new ItemStack(material);
                }

                ItemMeta meta = displayItem.getItemMeta();
                if (meta == null) continue;

                // Ghi đè tên và lore nếu có trong shop.yml
                if (itemConfig.contains("display-name")) {
                    meta.setDisplayName(ChatUtil.colorize(itemConfig.getString("display-name")));
                }
                
                List<String> newLore = new ArrayList<>(meta.getLore() != null ? meta.getLore() : new ArrayList<>());
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
    }

    public void open(Player player) {
        player.openInventory(shopInventory);
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != shopInventory) return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        File shopFile = new File(plugin.getDataFolder(), "shop.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(shopFile);
        
        String slotStr = String.valueOf(event.getSlot());
        ConfigurationSection itemConfig = config.getConfigurationSection("shop.items." + slotStr);
        if (itemConfig == null) return;
        
        // Xử lý nút đóng
        if(itemConfig.getBoolean("close_on_click", false)) {
            player.closeInventory();
            return;
        }

        // Xử lý mua bán
        double price = itemConfig.getDouble("price");
        String itemId = itemConfig.getString("item_id");
        if (price <= 0 || itemId == null) return; // Không phải vật phẩm có thể mua

        Economy econ = plugin.getEconomy();
        if (econ == null) {
            player.sendMessage(ChatUtil.colorize("&cHe thong kinh te khong hoat dong. Vui long lien he Admin."));
            return;
        }
        
        // Kiểm tra tiền
        if (econ.getBalance(player) < price) {
            String message = plugin.getConfig().getString("messages.not-enough-money")
                .replace("%cost%", String.format("%,.2f", price))
                .replace("%balance%", String.format("%,.2f", econ.getBalance(player)));
            player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));
            return;
        }
        
        // Trừ tiền và đưa vật phẩm
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