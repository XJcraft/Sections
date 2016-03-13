package org.jim.section.cmds;

import java.util.Arrays;

import org.bukkit.entity.Player;
import org.jim.section.Section;
import org.jim.section.SectionManager;
import org.jim.section.util.BoolUtility;
import org.jim.section.util.Utility;

public class FlagSet extends SubCommand {

	public FlagSet(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "set";
		this.extra = "<section> <flag> <value>";
		this.usage = "flag: " + Utility.join(Arrays.asList(Section.FLAGS), ",");
		this.permission = "section.set";
	}

	// sec set name flag value
	@Override
	public boolean onCommand(Player player, String[] args) {
		if (args.length < 3)
			return false;
		String name = args[0];
		String flag = args[1].toLowerCase();
		Boolean flagValue = BoolUtility.parse(args[2]);
		if (flagValue == null) {
			return false;
		}
		Section sec = SectionManager.me().getSection(name);
		if (sec == null) {
			SectionManager.me().sendMsg(player, "section.noExist");
			return true;
		}
		if (!player.isOp() && !sec.isCreater(player)) {
			SectionManager.me().sendMsg(player, "section.noPermission");
			return true;
		}
		if (sec.setFlag(flag, flagValue)) {
			SectionManager.me().sendMsg(player, "section.set.success", sec.getName(), flag, flagValue);
		} else {
			SectionManager.me().sendMsg(player, "section.set.fail");
		}
		return true;
	}

}
