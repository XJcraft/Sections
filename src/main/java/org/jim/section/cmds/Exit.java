package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.Section;
import org.jim.section.SectionManager;
import org.jim.section.util.Utility;

public class Exit extends SubCommand {

	public Exit(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "exit";
		this.extra = "<section>";
		this.permission = "section.exit";
	}

	// sec exit name
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("该命令限制为玩家使用");
			return true;
		}
		Player player = (Player) sender;
		Section sec = SectionManager.me().getSection(Utility.get(args, 0));
		if (sec == null) {
			SectionManager.me().sendMsg(sender, "section.noExist");
			return true;
		}
		if(!sec.hasJoin(player)){
			sender.sendMessage("§c你没有加入这个坑!");
			return true;
		}
		sec.exit(player);
		return true;
	}

}
