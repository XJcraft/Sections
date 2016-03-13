package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.SectionManager;
import org.jim.section.Section;
import org.jim.section.util.Utility;

public class Create extends SubCommand {

	public Create(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "create";
		this.extra = "<section>";
		this.permission = "section.create";
	}

	// sec create name
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("该命令限制为玩家使用");
			return true;
		}
		Player player = (Player) sender;
		String name = Utility.get(args, 0);
		if (name == null)
			return false;
		Section sec = SectionManager.me().getSection(name);
		if (sec != null) {
			SectionManager.me().sendMsg(sender, "section.hadExit");
			return true;
		}
		SectionManager.me().create(name, player);
		return true;
	}

}
