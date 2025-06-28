package vn.tutienhi.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.spigotmc.event.entity.EntityDismountEvent;
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
        if (plugin.getPlayerDataManager().getPlayerData(player) != null && plugin.getPlayerDataManager().getPlayerData(player).isCultivating()) {
            plugin.getCultivationTask().stopCultivating(player);
        }
        plugin.getPlayerDataManager().unloadPlayerData(player);
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
    
    // An extra check to stop cultivating if player is forced to dismount
    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data != null && data.isCultivating()) {
             plugin.getCultivationTask().stopCultivating(player);
        }
    }
}