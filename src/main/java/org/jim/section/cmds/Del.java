package org.jim.section.cmds;

import org.bukkit.command.CommandSender;
import org.jim.section.Section;
import org.jim.section.SectionManager;
import org.jim.section.util.Utility;

public class Del extends SubCommand {

	public Del(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "del";
		this.extra = "<section>";
		this.permission = "section.del";
	}

	// sec del name
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		String name = Utility.get(args, 0);
		Section sec = SectionManager.me().getSection(name);
		if (sec == null) {
			SectionManager.me().sendMsg(sender, "section.noExist");
			return true;
		}
		if (SectionManager.me().delete(sender,sec))
			SectionManager.me().sendMsg(sender, "section.delete.success",
					sec.getName());
		else
			SectionManager.me().sendMsg(sender, "section.delete.fail",
					sec.getName());
		return true;
	}

}
