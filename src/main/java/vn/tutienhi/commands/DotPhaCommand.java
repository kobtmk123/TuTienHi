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
import vn.tutienhi.models.Realm; // THÊM DÒNG IMPORT NÀY
import vn.tutienhi.utils.ChatUtil;

public class DotPhaCommand implements CommandExecutor {

    private final TuTienHi plugin;

    public DotPhaCommand(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            // ...
            return true;
        }

        Player player = (Player) sender;
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return true;

        // Sửa lại ở đây
        Realm currentRealm = plugin.getRealmManager().getRealm(data.getRealmId());
        if (currentRealm == null) return true;

        if (data.getLinhKhi() < currentRealm.getMaxLinhKhi()) {
            // ...
            return true;
        }
        
        // Và ở đây
        Realm nextRealm = plugin.getRealmManager().getNextRealm(currentRealm.getId());
        if (nextRealm == null) {
            // ...
            return true;
        }
        
        // ... code còn lại ...
        
        String message = plugin.getConfig().getString("messages.breakthrough-success")
                .replace("%new_realm_name%", nextRealm.getDisplayName());
        player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));

        return true;
    }
}