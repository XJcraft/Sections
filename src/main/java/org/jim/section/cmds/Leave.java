package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.Section;
import org.jim.section.SectionManager;

public class Leave extends SubCommand {

	public Leave(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "leave";
		this.extra = "";
		this.permission = "section.leave";
	}

	//sec leave
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("该命令限制为玩家使用");
			return true;
		}
		Player player = (Player)sender;
		Section sec = SectionManager.me().getPlayingSection(player);
		if(sec ==null){
			SectionManager.me().sendMsg(player, "section.noIn");
			return true;
		}
		sec.leave(player);
		return true;
	}

}
