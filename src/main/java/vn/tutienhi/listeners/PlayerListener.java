package vn.tutienhi.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.managers.CultivationPathManager;
import vn.tutienhi.models.Realm; // Đã thêm import

public class PlayerListener implements Listener {

    private final TuTienHi plugin;
    public PlayerListener(TuTienHi plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().loadPlayerData(player);
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            plugin.getRealmManager().applyRealmBonuses(player);
            plugin.getCultivationPathManager().applyPathBonus(player);
        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data != null && data.isCultivating()) {
            plugin.getCultivationTask().stopCultivating(player);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        // Đã sửa kiểu dữ liệu
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        CultivationPathManager.Path path = plugin.getCultivationPathManager().getPath(data.getCultivationPathId());

        if (realm == null || path == null) return;

        double originalDamage = event.getDamage();
        double bonusDamage = realm.getBonusDamage();
        double finalDamage;
        
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (path.getId().equals("kiemtu") && itemInHand.getType().name().endsWith("_SWORD")) {
            finalDamage = (originalDamage * path.getSwordDamageMultiplier()) + bonusDamage;
        } else {
            finalDamage = (originalDamage * path.getDamageMultiplier()) + bonusDamage;
        }
        
        event.setDamage(finalDamage);
    }
}