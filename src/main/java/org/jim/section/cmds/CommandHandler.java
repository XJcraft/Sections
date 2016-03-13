package org.jim.section.cmds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jim.section.AuditProxy;
import org.jim.section.SectionException;
import org.jim.section.SectionManager;
import org.jim.section.SectionPlugin;
import org.jim.section.util.Log;

public class CommandHandler implements TabExecutor {
	private static CommandHandler instance;
	private SectionPlugin plugin = null;
	private AuditProxy proxy;
	private Map<String, SubCommand> commands = new LinkedHashMap<>();

	public CommandHandler(SectionPlugin plugin) {
		this.plugin = plugin;
		instance = this;
		proxy = new AuditProxy(plugin);
		loadCommands();
	}

	private void loadCommands() {
		commands.put("create", new Create(this));
		commands.put("join", new Join(this));
		commands.put("set", new FlagSet(this));
		commands.put("leave", new Leave(this));
		commands.put("leaveall", new Leaveall(this));
		commands.put("help", new Help(this));
		// commands.put("load", new Load(this));
		// commands.put("save", new Save(this));
		// commands.put("saveall", new Saveall(this));
		commands.put("del", new Del(this));
		commands.put("exit", new Exit(this));
		commands.put("tp", new Tp(this));
		commands.put("list", new List(this));
		commands.put("plugin", new PluginCmd(this));
		//commands.put("reload", new List(this));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		if (!proxy.hasPermission(sender)) {
			SectionManager.me().sendMsg(sender, "section.noPermission");
			return true;
		}
		if (args == null || args.length < 1) {
			// sender.sendMessage("send /sec help for more info");
			commands.get("help").onCommand(sender, args);
			return true;
		}
		SubCommand sub = commands.get(args[0].toLowerCase());
		try {
			if (sub != null && sender.hasPermission(sub.getPermission())) {
				if (args.length >= 2) {
					LinkedList<String> list = new LinkedList<>();
					list.addAll(Arrays.asList(args));
					list.removeFirst();
					return sub.onCommand(sender,
							list.toArray(new String[list.size()]));
				} else
					return sub.onCommand(sender, new String[0]);
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "错误：" + e.getMessage());
			if (!(e instanceof SectionException))
				e.printStackTrace();
			else
				Log.warn("error: %s", e.getMessage());
			return true;
		}
		return false;
	}

	public static CommandHandler getInstance() {
		return instance;
	}

	public Map<String, SubCommand> getCommands() {
		return commands;
	}

	@Override
	public java.util.List<String> onTabComplete(CommandSender sender,
			Command command, String alias, String[] args) {
		if (!proxy.hasPermission(sender)) {
			SectionManager.me().sendMsg(sender, "section.noPermission");
			return null;
		}
		if (args == null || args.length < 1) {
			commands.get("help").onCommand(sender, args);
			return null;
		}
		java.util.List<String> result = new ArrayList<>();
		if (args.length == 1) {
			String name = args[0].toLowerCase();
			for (String key : commands.keySet())
				if (key.startsWith(name))
					result.add(key);
		} else if (args.length > 1) {
			SubCommand s = getCommand(args[0].toLowerCase());
			if (s != null) {
				sender.sendMessage(s.toHelp());
			}
		}
		return result;
	}

	public SubCommand getCommand(String name) {
		if (name == null)
			return null;
		for (String key : commands.keySet()) {
			if (key.startsWith(name))
				return commands.get(key);
		}
		return null;
	}

}
