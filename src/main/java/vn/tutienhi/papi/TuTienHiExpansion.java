package vn.tutienhi.papi;

// ... các import khác
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.managers.CultivationPathManager;
import vn.tutienhi.models.Realm; // THÊM DÒNG IMPORT NÀY

public class TuTienHiExpansion extends PlaceholderExpansion {
    // ...

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        // ...
        Player player = offlinePlayer.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) {
            return "Loading...";
        }

        // Sửa lại ở đây
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) {
            return "Unknown Realm";
        }
        
        // ... code còn lại ...
    }
}