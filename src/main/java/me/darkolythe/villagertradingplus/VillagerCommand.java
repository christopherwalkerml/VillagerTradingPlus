package me.darkolythe.villagertradingplus;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class VillagerCommand implements CommandExecutor {

    private VillagerTradingPlus main = VillagerTradingPlus.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandlabel, String[] args) {
        Player player = (Player) sender;

        if (player.hasPermission("villagertradingplus.reload")) {
            if (cmd.getName().equalsIgnoreCase("villagertradingplus")) { //if the player has permission, and the command is right
                if (args.length == 0) {
                    sender.sendMessage(VillagerTradingPlus.prefix + ChatColor.RED + "Invalid Arguments: /vtp reload");
                } else if (args[0].equalsIgnoreCase("reload")) {
                    main.getConfigYML();
                    sender.sendMessage(VillagerTradingPlus.prefix + ChatColor.GREEN + "VillagerTradingPlus config reloaded!");
                } else {
                    sender.sendMessage(VillagerTradingPlus.prefix + ChatColor.RED + "Invalid Arguments: /vtp reload");
                }
            }
        }
        return true;
    }
}
