package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.SectionManager;
import org.jim.section.Section;
import org.jim.section.util.Utility;

public class Join extends SubCommand {

	public Join(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "join";
		this.extra = "<section>";
		this.permission = "section.join";
	}

	// sec join name
	@Override
	public boolean onCommand(Player player, String[] args) {
		String name = Utility.get(args, 0);
		if (name == null)
			return false;
		Section sec = SectionManager.me().getSection(name);
		if (sec == null) {
			player.sendMessage("§c没有这个坑吧...");
			return true;
		}
		if (sec.isPlaying(player)) {
			player.sendMessage(String.format("§6%s: §b你已经加入坑了", sec.getName()));
			return true;
		}
		Section playeringSec = SectionManager.me().getPlayingSection(player);
		if (playeringSec != null) {
			player.sendMessage(String.format("§6%s: §b你已经加入坑了", playeringSec.getName()));
			return true;
		}
		sec.join(player);
		return true;
	}

}
