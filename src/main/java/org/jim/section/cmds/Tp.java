package org.jim.section.cmds;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.Section;
import org.jim.section.SectionManager;
import org.jim.section.util.Utility;

public class Tp extends SubCommand {

	public Tp(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "tp";
		this.extra = "<player>";
		this.permission = "section.tp"; 
	}

	// sec tp player
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("该命令限制为玩家使用");
			return true;
		}
		Player player = (Player) sender;
		Section sec = SectionManager.me().getPlayingSection(player);
		if (sec == null) {
			// sender.sendMessage("你不在坑里……");
			SectionManager.me().sendMsg(player, "section.noIn");
			return true;
		}
		String name = Utility.get(args, 0);
		if (name == null)
			return false;
		Player tpPlayer = Bukkit.getPlayer(name);
		if (tpPlayer == null) {
			player.sendMessage(name + "不在线或不存在");
			return true;
		}
		if (sec.getFlag("tp")) {
			if (sec.isPlaying(tpPlayer))
				if(player.teleport(tpPlayer)){
					sec.broadcastMsg("§6%s: §f传送 §b%s§f 到 §b%s ", sec.getName(),player.getName(),tpPlayer.getName());
					player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, null);
					player.getWorld().playSound(player.getLocation(), Sound.BLOCK_PORTAL_TRAVEL, 3, 1);
				}
			else
				player.sendMessage(String.format("§6%s: §cTa不在这个坑里……",sec.getName()));
		} else {
			SectionManager.me().sendMsg(player, "section.noPermission");
		}
		return true;
	}; 

}
