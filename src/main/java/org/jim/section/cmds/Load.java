package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.SectionManager;
import org.jim.section.Section;
import org.jim.section.util.Utility;

public class Load extends SubCommand {

	public Load(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "load";
		this.extra = "[section]";
		this.permission = "section.load"; 
	}

	//sec load name
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if(!(sender instanceof Player)){
			sender.sendMessage("该命令限制为玩家使用");
			return true;
		}
		Player player = (Player)sender;
		String name = Utility.get(args, 0);
		if(name == null){
			Section playingSec = SectionManager.me().getPlayingSection(player);
			load(playingSec, player);
		}else{
			Section sec = SectionManager.me().getSection(name);
			Section playingSec = SectionManager.me().getPlayingSection(player);
			if(sec == null && playingSec == null){
				//player.sendMessage("no exist");
				SectionManager.me().sendMsg(sender, "section.noExist");
				return true;
			}else if(sec == null && playingSec != null){
				load(playingSec, player);
			}else if(sec!= null && playingSec == null){
				load(sec, player);
			}else {
				if(sec.equals(playingSec)){
					load(playingSec, player);
				}else{
					//player.sendMessage("你在坑("+playingSec.getName()+")不能跳坑哦");
					SectionManager.me().sendMsg(player, "section.in",sec.getName());
				}
			}
		}
		return true; 
	}

	private void load(Section sec,Player player){ 
		/*if(sec ==null){
			GameManager.me().sendMsg(player, "section.noExist");
			return ;
		}
		if(sec.hasJoin(player)){
			GameManager.me().sendMsg(player, "section.join.had",sec.getName());
			return ;
		}
		if(GameManager.me().load(player))
			GameManager.me().sendMsg(player, "section.load.success",sec.getName());
		else
			GameManager.me().sendMsg(player, "section.noPermission");*/
	}
}
