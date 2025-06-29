package vn.tutienhi.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import vn.tutienhi.TuTienHi;
import vn.tutienhi.gui.ShopGUI;
import vn.tutienhi.utils.ChatUtil;

public class ShopCommand implements CommandExecutor {

    private final TuTienHi plugin;

    public ShopCommand(TuTienHi plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatUtil.colorize(plugin.getConfig().getString("messages.player-only-command")));
            return true;
        }
        
        Player player = (Player) sender;
        ShopGUI shopGUI = new ShopGUI(plugin);
        shopGUI.open(player);

        return true;
    }
}