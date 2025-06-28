package vn.tutienhi.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
// Không cần import EntityDismountEvent nữa
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;

public class PlayerListener implements Listener {

    private final TuTienHi plugin;

    public PlayerListener(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getPlayerDataManager().loadPlayerData(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // Đảm bảo dừng tu luyện khi người chơi thoát
        if (plugin.getPlayerDataManager().getPlayerData(player) != null && plugin.getPlayerDataManager().getPlayerData(player).isCultivating()) {
            plugin.getCultivationTask().stopCultivating(player);
        }
        plugin.getPlayerDataManager().unloadPlayerData(player);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        // Chỉ kích hoạt khi bắt đầu sneak (nhấn Shift)
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Nếu người chơi đang tu luyện, dừng lại
        if (data != null && data.isCultivating()) {
            plugin.getCultivationTask().stopCultivating(player);
        }
    }
    
    // Đã xóa phương thức onDismount để tránh lỗi biên dịch
}