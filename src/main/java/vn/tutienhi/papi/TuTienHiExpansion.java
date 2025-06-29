package vn.tutienhi.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.managers.CultivationPathManager;
import vn.tutienhi.models.Realm; // Đã thêm import
import vn.tutienhi.utils.ChatUtil;

public class TuTienHiExpansion extends PlaceholderExpansion {

    private final TuTienHi plugin;

    public TuTienHiExpansion(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() { return "tutienhi"; }
    @Override
    public @NotNull String getAuthor() { return "T"; }
    @Override
    public @NotNull String getVersion() { return plugin.getDescription().getVersion(); }
    @Override
    public boolean persist() { return true; }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return "";
        }

        Player player = offlinePlayer.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) {
            return "Dang tai...";
        }

        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) {
            return "Khong ro";
        }
        
        CultivationPathManager.Path path = plugin.getCultivationPathManager().getPath(data.getCultivationPathId());
        if (path == null) {
             path = plugin.getCultivationPathManager().getPath("none");
        }

        switch (params.toLowerCase()) {
            case "canhgioi":
            case "realm_name":
                return realm.getDisplayName();
            case "linhkhi":
            case "linhkhi_current":
                return String.format("%,.0f", data.getLinhKhi());
            case "linhkhi_max":
                return String.format("%,.0f", realm.getMaxLinhKhi());
            case "linhkhi_full":
                return String.format("%,.0f/%,.0f", data.getLinhKhi(), realm.getMaxLinhKhi());
            case "linhkhi_percent":
                if (realm.getMaxLinhKhi() <= 0) return "0";
                double percent = (data.getLinhKhi() / realm.getMaxLinhKhi()) * 100;
                return String.format("%.1f", percent);
            case "path_name":
                return ChatUtil.colorize(path.getDisplayName());
            default:
                return null;
        }
    }
}