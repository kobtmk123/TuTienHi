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
        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + plugin.getConfig().getString("messages.item-give-usage")));
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.player-not-found").replace("%player%", args[2])));
            return;
        }

        String itemId = args[3].toLowerCase();
        ItemStack item = plugin.getItemManager().getItem(itemId);
        if (item == null) {
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + 
                plugin.getConfig().getString("messages.item-not-found").replace("%id%", itemId)));
            return;
        }

        int amount = 1;
        if (args.length > 4) {
            try {
                amount = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatUtil.colorize("&cSo luong khong hop le."));
                return;
            }
        }

        item.setAmount(amount);
        target.getInventory().addItem(item);

        String message = plugin.getConfig().getString("messages.item-give-success")
            .replace("%amount%", String.valueOf(amount))
            .replace("%item_name%", item.getItemMeta().getDisplayName())
            .replace("%player%", target.getName());
        sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.prefix") + message));
    }
    
    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage(ChatUtil.colorize("&b&l--- TuTienHi Admin v2.0 ---"));
        sender.sendMessage(ChatUtil.colorize("&e/tth reload &7- Tai lai cac file config."));
        sender.sendMessage(ChatUtil.colorize("&e/tth vatpham give <player> <id> [amount] &7- Trao vat pham."));
    }
}