package vn.tutienhi.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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

        if (args.length == 0) {
            sendHelpMessage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        if (subCommand.equals("reload")) {
            // SỬA LỖI: Gọi đúng tên hàm reloadAllConfigs()
            plugin.reloadAllConfigs();
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.reload-success")));
            return true;
        }

        if (subCommand.equals("vatpham")) {
            handleItemCommand(sender, args);
            return true;
        }

        sendHelpMessage(sender);
        return true;
    }

    private void handleItemCommand(CommandSender sender, String[] args) {
        // ... code handleItemCommand giữ nguyên ...
    }
    
    private void sendHelpMessage(CommandSender sender) {
        // ... code sendHelpMessage giữ nguyên ...
    }
}