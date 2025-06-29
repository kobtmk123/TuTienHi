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

            if (data.isCultivating() && cultivatingStands.containsKey(player.getUniqueId())) {
                ArmorStand stand = cultivatingStands.get(player.getUniqueId());
                if (player.getVehicle() != stand) {
                     stopCultivating(player);
                } else {
                    // Giữ người chơi ở đúng vị trí để tránh bị đẩy đi
                    if (!player.getLocation().equals(stand.getLocation())) {
                        player.teleport(stand.getLocation());
                    }
                }
            }

            if (data.isCultivating()) {
                handleCultivation(player, data);
            }
            updateScoreboard(player, data);
        }
    }
    
    private void handleCultivation(Player player, PlayerData data) {
        RealmManager.Realm realm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (realm == null) return;
        double baseGain = realm.getLinhKhiPerTick();
        double zoneMultiplier = plugin.getZoneManager().getMultiplierAt(player.getLocation());
        double finalGain = baseGain * zoneMultiplier;
        if (data.getLinhKhi() < realm.getMaxLinhKhi()) {
            data.addLinhKhi(finalGain);
            if (data.getLinhKhi() > realm.getMaxLinhKhi()) {
                data.setLinhKhi(realm.getMaxLinhKhi());
            }
        }
        spawnParticles(player);
    }
    
    private void spawnParticles(Player player) {
        PlayerData pData = plugin.getPlayerDataManager().getPlayerData(player);
        if (pData == null) return;

        RealmManager.Realm realm = plugin.getRealmManager().getRealm(pData.getRealmId());
        if (realm == null) return;

        int totalRealms = plugin.getRealmManager().getTotalRealms();
        int currentRealmIndex = plugin.getRealmManager().getRealmOrder().indexOf(realm.getId());

        String particleName;
        if (totalRealms > 3 && currentRealmIndex >= totalRealms - 3) {
            particleName = plugin.getConfig().getString("settings.particle-effect.final-realms-particle", "FLAME").toUpperCase();
        } else {
            particleName = plugin.getConfig().getString("settings.particle-effect.default", "CLOUD").toUpperCase();
        }
        
        Particle particle;
        try { particle = Particle.valueOf(particleName); } catch (Exception e) { particle = Particle.CLOUD; }
        
        int count = plugin.getConfig().getInt("settings.particle-effect.count", 10);
        double radius = plugin.getConfig().getDouble("settings.particle-effect.radius", 1.0);
        Location loc = player.getLocation();

        for (int i = 0; i < 360; i += 360 / count) {
            double angle = (i * Math.PI / 180);
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            loc.getWorld().spawnParticle(particle, loc.clone().add(x, 1, z), 1, 0, 0, 0, 0);
        }
    }

    private void updateScoreboard(Player player, PlayerData data) {
        // ... (Code update scoreboard giữ nguyên, không cần sửa)
    }
    
    public void startCultivating(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null || data.isCultivating()) return;
        
        data.setCultivating(true);
        
        Location playerLoc = player.getLocation();
        // Vị trí lơ lửng cách block dưới chân 2 block
        Location standLoc = playerLoc.getBlock().getLocation().add(0.5, 1.0, 0.5);

        ArmorStand stand = player.getWorld().spawn(standLoc, ArmorStand.class, as -> {
            as.setGravity(false);
            as.setVisible(false);
            as.setMarker(true);
            as.setInvulnerable(true);
        });
        stand.addPassenger(player);
        cultivatingStands.put(player.getUniqueId(), stand);
        player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.cultivate-start")));
    }

    public void stopCultivating(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null || !data.isCultivating()) return;
        data.setCultivating(false);
        ArmorStand stand = cultivatingStands.remove(player.getUniqueId());
        if (stand != null) {
            stand.remove();
        }
        player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.cultivate-stop")));
    }

    public void stopAllCultivation() {
        new HashMap<>(cultivatingStands).forEach((uuid, stand) -> {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null && p.isOnline()) { stopCultivating(p); } 
            else if (stand != null) { stand.remove(); }
        });
        cultivatingStands.clear();
    }
}