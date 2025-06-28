package vn.tutienhi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.utils.ChatUtil;

public class AdminCommand implements CommandExecutor {

    private final TuTienHi plugin;

    public AdminCommand(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("tutienhi.admin")) {
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelpMessage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadPluginConfigs();
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.reload-success")));
            return true;
        }

        sendHelpMessage(sender);
        return true;
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatUtil.colorize("&b&l--- TuTienHi Admin ---"));
        sender.sendMessage(ChatUtil.colorize("&e/" + "tth" + " help &7- Hien thi trang tro giup nay."));
        sender.sendMessage(ChatUtil.colorize("&e/" + "tth" + " reload &7- Tai lai cac file config cua plugin."));
    }
}