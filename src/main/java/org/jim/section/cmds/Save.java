package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.SectionManager;
import org.jim.section.Section;

public class Save extends SubCommand {

	public Save(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "save";
		this.extra = "";
		this.permission = "section.save"; 
	}
 
	//sec save
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
		//GameManager.me().save(sec, player);
		sec.save(player);
		return true;
	};

}
