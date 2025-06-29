package vn.tutienhi.managers;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.utils.ChatUtil;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemManager {

    private final TuTienHi plugin;
    private final Map<String, ItemStack> customItems = new HashMap<>();

    public ItemManager(TuTienHi plugin) {
        this.plugin = plugin;
        loadItems();
    }

    public void loadItems() {
        customItems.clear();
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        if (!itemsFile.exists()) {
            plugin.saveResource("items.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(itemsFile);
        ConfigurationSection itemsSection = config.getConfigurationSection("items");
        if (itemsSection == null) return;

        for (String id : itemsSection.getKeys(false)) {
            ConfigurationSection itemSection = itemsSection.getConfigurationSection(id);
            if (itemSection == null) continue;

            try {
                Material material = Material.valueOf(itemSection.getString("material", "PAPER").toUpperCase());
                ItemStack item = new ItemStack(material);
                ItemMeta meta = item.getItemMeta();

                if (meta != null) {
                    meta.setDisplayName(ChatUtil.colorize(itemSection.getString("display-name", "Unnamed Item")));
                    List<String> lore = itemSection.getStringList("lore").stream()
                            .map(ChatUtil::colorize)
                            .collect(Collectors.toList());
                    meta.setLore(lore);

                    if (itemSection.contains("custom-model-data")) {
                        meta.setCustomModelData(itemSection.getInt("custom-model-data"));
                    }
                    
                    // Thêm NBT tag để nhận diện vật phẩm
                    meta.getPersistentDataContainer().set(plugin.getNamespacedKey(), PersistentDataType.STRING, id);
                    
                    item.setItemMeta(meta);
                    customItems.put(id, item);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Vat lieu khong hop le trong items.yml cho ID: " + id);
            }
        }
        plugin.getLogger().info("Da tai " + customItems.size() + " vat pham tuy chinh.");
    }

    public ItemStack getItem(String id) {
        return customItems.get(id) != null ? customItems.get(id).clone() : null;
    }
    
    public List<String> getItemActions(String id) {
        File itemsFile = new File(plugin.getDataFolder(), "items.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(itemsFile);
        return config.getStringList("items." + id + ".actions");
    }
}