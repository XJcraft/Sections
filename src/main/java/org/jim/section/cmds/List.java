package org.jim.section.cmds;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jim.section.Section;
import org.jim.section.SectionManager;

public class List extends SubCommand {

	private DateFormat format = new SimpleDateFormat("YYYY/MM/dd HH:mm");

	public List(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "list";
		this.extra = "";
		this.permission = "section.list";
	}

	// sec exit name
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		java.util.List<Section> secs = SectionManager.me().getAllSections();
		StringBuffer sb = new StringBuffer();
		sb.append("---Section List----\n");
		for (Section sec : secs) {
			sb.append("坑" + ChatColor.AQUA + sec.getName() + ChatColor.WHITE);
			sb.append(" 创建人： " + ChatColor.BLUE + sec.getCreatePlayer() + ChatColor.WHITE);
			sb.append(" 创建时间： " + format.format(new Date(sec.getCreateTime())));
			sb.append(" 参与人数： " + ChatColor.UNDERLINE + sec.getPlayingPlayer().size() + "/" + sec.getAllPlayer().size()
					+ ChatColor.WHITE);
			sb.append("\n");
		}
		sender.sendMessage(sb.toString());
		return true;
	}

}
