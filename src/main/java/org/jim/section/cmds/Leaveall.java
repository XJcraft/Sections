package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.Section;
import org.jim.section.SectionManager;

public class Leaveall extends SubCommand {

	public Leaveall(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "leaveall";
		this.extra = "";
		this.permission = "section.leaveall";
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
		if(!sec.isCreater(player)){
			SectionManager.me().sendMsg(sender, "section.noPermission");
			return true;
		}
		/*for(Player p: sec.getOnlinePlayer()){
			sec.broadcast("section.leave.success",p.getName(),sec.getName());
		}
		GameManager.me().leaveAll(sec);*/
		sec.leaveall();
		return true;
	}

}
