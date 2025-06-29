package vn.tutienhi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.data.PlayerData;
import vn.tutienhi.managers.CultivationPathManager;
import vn.tutienhi.utils.ChatUtil;
import java.util.List;

public class CultivationPathCommand implements CommandExecutor {

    private final TuTienHi plugin;

    public CultivationPathCommand(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.player-only-command")));
            return true;
        }
        
        Player player = (Player) sender;
        String commandName = command.getName().toLowerCase();
        
        if (commandName.equals("conduongtutap")) {
            List<String> helpMessage = plugin.getConfig().getStringList("messages.cultivation-path-help");
            helpMessage.forEach(line -> player.sendMessage(ChatUtil.colorize(line)));
            return true;
        }
        
        String pathId = null;
        switch (commandName) {
            case "kiemtu":
                pathId = "kiemtu";
                break;
            case "phattu":
                pathId = "phattu";
                break;
            case "matu":
                pathId = "matu";
                break;
        }
        
        if (pathId != null) {
            choosePath(player, pathId);
        }
        return true;
    }
    
    private void choosePath(Player player, String pathId) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        if (data == null) return;
        
        if (!data.getCultivationPathId().equals("none")) {
            player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.path-already-chosen")));
            return;
        }
        
        data.setCultivationPathId(pathId);
        plugin.getCultivationPathManager().applyPathBonus(player);
        
        CultivationPathManager.Path path = plugin.getCultivationPathManager().getPath(pathId);
        String message = plugin.getConfig().getString("messages.path-chosen")
            .replace("%path_name%", ChatUtil.colorize(path.getDisplayName()));
        player.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));
    }
}