package org.jim.section.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {

	protected String name;
	protected String extra = "";
	protected String usage = "";
	protected String permission;
	private CommandHandler commandHandler;

	public SubCommand(CommandHandler commandHandler) {
		super();
		this.commandHandler = commandHandler;
	}

	public SubCommand(String name, String extra, String usage, String permission, CommandHandler commandHandler) {
		super();
		this.name = name;
		this.extra = extra;
		this.usage = usage;
		this.permission = permission;
		this.commandHandler = commandHandler;
	}

	public CommandHandler getCommandHandler() {
		return commandHandler;
	}

	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("该命令限制为玩家使用");
			return true;
		}
		return onCommand((Player) sender, args);
	};

	public boolean onCommand(Player sender, String[] args) {
		return false;
	};

	public String toHelp() {
		return ChatColor.AQUA + "sec" + ChatColor.BLUE + " " + name + ChatColor.WHITE + " " + extra + " -->" + usage;
	}

	public String getPermission() {
		return permission;
	}

}
