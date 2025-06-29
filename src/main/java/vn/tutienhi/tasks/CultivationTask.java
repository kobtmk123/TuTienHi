package vn.tutienhi.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.models.Realm; // Đã thêm import
import vn.tutienhi.utils.ChatUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CultivationTask extends BukkitRunnable {
    // ...

    private void handleCultivation(Player player, PlayerData data) {
        // Đã sửa kiểu dữ liệu
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) return;
        // ...
    }
    
    private void spawnParticles(Player player) {
        PlayerData pData = plugin.getPlayerDataManager().getPlayerData(player);
        if (pData == null) return;

        // Đã sửa kiểu dữ liệu
        Realm realm = plugin.getRealmManager().getRealm(pData.getRealmId());
        if (realm == null) return;
        // ...
    }

    private void updateScoreboard(Player player, PlayerData data) {
        // ...
        // Đã sửa kiểu dữ liệu
        Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) return;
        // ...
    }
    // ...
}