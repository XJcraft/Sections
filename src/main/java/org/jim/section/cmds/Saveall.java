package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Saveall extends SubCommand {

	public Saveall(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "saveall";
		this.extra = "";
		this.permission = "section.saveall"; 
	}
 
	//sec save
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("该命令限制为玩家使用");
			return true;
		}
		/*Player player = (Player)sender;
		Section sec = GameManager.me().getPlayingSection(player);
		if(sec ==null){
			GameManager.me().sendMsg(player, "section.noIn");
			return true;
		}
		if(!sec.isCreatePlayer(player)){
			GameManager.me().sendMsg(sender, "section.noPermission");
			return true;
		}
		//sec.save(player);
		sec.broadcast("section.save.success", sec.getName());*/
		//GameManager.me().sendMsg(player, "section.save.success", sec.getName());
		return true;
	}; 

}
