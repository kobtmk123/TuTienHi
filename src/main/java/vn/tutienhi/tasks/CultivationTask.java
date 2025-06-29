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
import vn.tutienhi.managers.RealmManager;
import vn.tutienhi.utils.ChatUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CultivationTask extends BukkitRunnable {

    private final TuTienHi plugin;
    private final Map<UUID, ArmorStand> cultivatingStands = new HashMap<>();

    public CultivationTask(TuTienHi plugin) { this.plugin = plugin; }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
            if (data == null) continue;

            // Nếu đang tu luyện, dịch chuyển lại vị trí để chống bị đẩy
            if (data.isCultivating() && cultivatingStands.containsKey(player.getUniqueId())) {
                ArmorStand stand = cultivatingStands.get(player.getUniqueId());
                if(!stand.getLocation().equals(player.getLocation().add(0,2,0))) {
                    player.teleport(stand.getLocation().clone().subtract(0,2,0));
                }
            }

            if (data.isCultivating()) {
                handleCultivation(player, data);
            }
            updateScoreboard(player);
        }
    }
    
    // ... các hàm khác ...

    private void spawnParticles(Player player) {
        RealmManager.Realm realm = plugin.getRealmManager().getRealm(plugin.getPlayerDataManager().getPlayerData(player).getRealmId());
        int totalRealms = plugin.getRealmManager().getTotalRealms();
        int currentRealmIndex = plugin.getRealmManager().getRealms().indexOf(realm);

        String particleName;
        // Nếu người chơi ở 1 trong 3 cảnh giới cuối
        if (totalRealms > 3 && currentRealmIndex >= totalRealms - 3) {
            particleName = plugin.getConfig().getString("settings.particle-effect.final-realms-particle", "FLAME").toUpperCase();
        } else {
            particleName = plugin.getConfig().getString("settings.particle-effect.default", "CLOUD").toUpperCase();
        }
        
        Particle particle;
        try { particle = Particle.valueOf(particleName); } catch (Exception e) { particle = Particle.CLOUD; }
        
        // ... code spawn particle giữ nguyên ...
    }
    
    public void startCultivating(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null || data.isCultivating()) return;
        
        data.setCultivating(true);

        // Vị trí mới: lơ lửng trên không 2 block
        Location standLoc = player.getLocation().clone().add(0, 2, 0);

        ArmorStand stand = player.getWorld().spawn(standLoc, ArmorStand.class, as -> {
            as.setGravity(false);
            as.setVisible(false);
            as.setMarker(true);
            as.setInvulnerable(true);
        });
        player.teleport(standLoc);
        stand.addPassenger(player);
        cultivatingStands.put(player.getUniqueId(), stand);

        player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.cultivate-start")));
    }
    
    // ... các hàm khác giữ nguyên ...
}