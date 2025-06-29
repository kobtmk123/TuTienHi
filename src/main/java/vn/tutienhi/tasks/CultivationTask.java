package vn.tutienhi.tasks;

// ... các import khác
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.models.Realm; // THÊM DÒNG IMPORT NÀY
import vn.tutienhi.utils.ChatUtil;

public class CultivationTask extends BukkitRunnable {
    // ...

    private void handleCultivation(Player player, PlayerData data) {
        // Sửa lại ở đây
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) return;
        // ... code còn lại ...
    }
    
    private void spawnParticles(Player player) {
        PlayerData pData = plugin.getPlayerDataManager().getPlayerData(player);
        if (pData == null) return;

        // Sửa lại ở đây
        Realm realm = plugin.getRealmManager().getRealm(pData.getRealmId());
        if (realm == null) return;

        // ... code còn lại ...
    }

    private void updateScoreboard(Player player, PlayerData data) {
        // ...
        // Sửa lại ở đây
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) return;

        // ... code còn lại ...
    }
    // ...
}