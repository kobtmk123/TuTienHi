package vn.tutienhi.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.utils.ChatUtil;
import java.util.List;

public class ItemListener implements Listener {

    private final TuTienHi plugin;

    public ItemListener(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(plugin.getNamespacedKey(), PersistentDataType.STRING)) {
            return;
        }

        String itemId = item.getItemMeta().getPersistentDataContainer().get(plugin.getNamespacedKey(), PersistentDataType.STRING);
        if (itemId == null) return;
        
        event.setCancelled(true);

        List<String> actions = plugin.getItemManager().getItemActions(itemId);
        if (actions.isEmpty()) return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        for (String action : actions) {
            String[] parts = action.split(":");
            if (parts.length != 2) continue;

            String actionType = parts[0].trim();
            String actionValue = parts[1].trim();

            if (actionType.equalsIgnoreCase("add_linhkhi")) {
                try {
                    double amount = Double.parseDouble(actionValue);
                    data.addLinhKhi(amount);
                    
                    String message = plugin.getConfig().getString("messages.item-usage", "")
                        .replace("%item_name%", item.getItemMeta().getDisplayName())
                        .replace("%amount%", String.valueOf((int)amount));
                    player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));

                    item.setAmount(item.getAmount() - 1);

                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Gia tri khong hop le cho 'add_linhkhi' trong items.yml: " + actionValue);
                }
            }
        }
    }
}