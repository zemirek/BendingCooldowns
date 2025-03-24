package me.literka;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BCooldownsCommand implements TabExecutor {

	public BCooldownsCommand() {
		PluginCommand command = BendingCooldowns.plugin.getCommand("bendingcooldowns");
		command.setExecutor(this);
		command.setTabCompleter(this);
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (!sender.hasPermission("bendingcooldowns.reload")) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to this command!");

			return true;
		} else if (args.length < 1 || !args[0].equalsIgnoreCase("reload")) {
			sender.sendMessage(ChatColor.RED + "Proper use: " + ChatColor.YELLOW + "/" + label + " reload");

			return true;
		}

		BendingCooldowns.onReload();
		sender.sendMessage(ChatColor.AQUA + "Successfully reloaded Bending Cooldowns");
		return true;
	}

	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		return args.length <= 1 ? List.of("reload") : List.of();
	}
}