package vn.tutienhi.papi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.managers.CultivationPathManager;
import vn.tutienhi.managers.RealmManager;
import vn.tutienhi.utils.ChatUtil;

public class TuTienHiExpansion extends PlaceholderExpansion {

    private final TuTienHi plugin;

    public TuTienHiExpansion(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        // Đây là tên để gọi placeholder, ví dụ: %tutienhi_...%
        return "tutienhi"; 
    }

    @Override
    public @NotNull String getAuthor() {
        return "T";
    }

    @Override
    public @NotNull String getVersion() {
        // Lấy phiên bản từ plugin.yml để tự động cập nhật
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        // true để giữ expansion được load ngay cả khi không có người chơi
        return true; 
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, @NotNull String params) {
        // Kiểm tra xem người chơi có online không
        if (offlinePlayer == null || !offlinePlayer.isOnline()) {
            return "";
        }

        Player player = offlinePlayer.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) {
            return "Dang tai...";
        }

        // Lấy thông tin cảnh giới và con đường tu luyện
        RealmManager.Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) {
            return "Khong ro"; // Trả về nếu có lỗi không tìm thấy cảnh giới
        }
        
        CultivationPathManager.Path path = plugin.getCultivationPathManager().getPath(data.getCultivationPathId());
        if (path == null) {
             path = plugin.getCultivationPathManager().getPath("none"); // Mặc định nếu chưa chọn
        }


        // Xử lý các placeholder được yêu cầu
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
                // Trả về null nếu placeholder không hợp lệ, PAPI sẽ tự xử lý
                return null; 
        }
    }
}