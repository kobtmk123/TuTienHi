package vn.tutienhi.listeners;

// ... các import khác
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.managers.CultivationPathManager;
import vn.tutienhi.models.Realm; // THÊM DÒNG IMPORT NÀY

public class PlayerListener implements Listener {
    // ...

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;

        // Sửa lại ở đây
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        CultivationPathManager.Path path = plugin.getCultivationPathManager().getPath(data.getCultivationPathId());

        if (realm == null || path == null) return;

        // ... code tính damage còn lại ...
    }
}