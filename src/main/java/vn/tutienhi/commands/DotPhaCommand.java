package vn.tutienhi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.managers.RealmManager;
import vn.tutienhi.utils.ChatUtil;

public class DotPhaCommand implements CommandExecutor {

    private final TuTienHi plugin;

    public DotPhaCommand(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.player-only-command")));
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return true;

        RealmManager.Realm currentRealm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (currentRealm == null) return true;

        if (data.getLinhKhi() < currentRealm.getMaxLinhKhi()) {
            String message = plugin.getConfig().getString("messages.breakthrough-fail-not-enough-linh-khi")
                    .replace("%required%", String.format("%,.0f", currentRealm.getMaxLinhKhi()))
                    .replace("%current%", String.format("%,.0f", data.getLinhKhi()));
            player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));
            return true;
        }
        
        RealmManager.Realm nextRealm = plugin.getRealmManager().getNextRealm(currentRealm.getId());
        if (nextRealm == null) {
            player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.breakthrough-fail-max-level")));
            return true;
        }
        
        // Đột phá thành công
        data.setRealmId(nextRealm.getId());
        data.setLinhKhi(0);
        
        // Hiệu ứng sét đánh
        if (nextRealm.getLightningDamage() > 0) {
            LightningStrike lightning = (LightningStrike) player.getWorld().spawnEntity(player.getLocation(), EntityType.LIGHTNING_BOLT);
            lightning.setCausingPlayer(player); // Để không gây sát thương cho chính người chơi
            // Thực tế, việc set sát thương của sét rất phức tạp, cách đơn giản là dùng hiệu ứng
            // player.damage(nextRealm.getLightningDamage()); // Dòng này có thể gây chết người chơi, cần cân nhắc
        }

        // Áp dụng các bonus của cảnh giới mới
        plugin.getRealmManager().applyRealmBonuses(player);
        plugin.getCultivationPathManager().applyPathBonus(player);

        String message = plugin.getConfig().getString("messages.breakthrough-success")
                .replace("%new_realm_name%", nextRealm.getDisplayName());
        player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));

        return true;
    }
}