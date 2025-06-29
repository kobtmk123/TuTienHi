package vn.tutienhi.listeners;

import org.bukkit.Material;
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
import vn.tutienhi.managers.RealmManager;

public class PlayerListener implements Listener {

    private final TuTienHi plugin;
    public PlayerListener(TuTienHi plugin) { this.plugin = plugin; }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().loadPlayerData(player);
        // Áp dụng lại bonus máu và hiệu ứng khi người chơi tham gia
        plugin.getRealmManager().applyRealmBonuses(player);
        plugin.getCultivationPathManager().applyPathBonus(player);
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        RealmManager.Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        CultivationPathManager.Path path = plugin.getCultivationPathManager().getPath(data.getCultivationPathId());

        if (realm == null || path == null) return;

        double originalDamage = event.getDamage();
        double finalDamage = originalDamage;

        // Cộng bonus damage từ cảnh giới
        finalDamage += realm.getBonusDamage();
        
        // Nhân với hệ số của con đường tu luyện
        finalDamage *= path.getDamageMultiplier();

        // Kiểm tra nếu là Kiếm Tu và đang dùng kiếm
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        if (itemInHand.getType().name().endsWith("_SWORD") && path.getId().equals("kiemtu")) {
            // Chỉ áp dụng bonus của Kiếm Tu lên phần sát thương gốc của vũ khí
            double baseWeaponDamage = originalDamage;
            finalDamage = (baseWeaponDamage * path.getSwordDamageMultiplier()) + realm.getBonusDamage();
        }

        event.setDamage(finalDamage);
    }
}