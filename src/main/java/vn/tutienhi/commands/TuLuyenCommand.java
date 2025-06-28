package vn.tutienhi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.utils.ChatUtil;

public class TuLuyenCommand implements CommandExecutor {
    private final TuTienHi plugin;

    public TuLuyenCommand(TuTienHi plugin) {
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

        if (data.isCultivating()) {
            player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.already-cultivating")));
        } else {
            plugin.getCultivationTask().startCultivating(player);
        }

        return true;
    }
}