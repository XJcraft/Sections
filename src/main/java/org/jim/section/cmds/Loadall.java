package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.Section;
import org.jim.section.SectionManager;

public class Loadall extends SubCommand {

	public Loadall(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "loadall";
		this.extra = "";
		this.permission = "section.loadall"; 
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
		//sec.broadcast(player.getName()+"离开了坑("+sec.getName()+")");
		sec.leaveall();
		return true;
	};
   
}
