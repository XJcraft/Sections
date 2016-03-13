package org.jim.section.cmds;

import org.bukkit.command.CommandSender;

public class Help extends SubCommand {

	public Help(CommandHandler commandHandler) {
		super(commandHandler);
		this.name = "help";
		this.extra = "";
		this.permission = "section.help";
	}

	//sec create name
	@Override
	public boolean onCommand(CommandSender sender, String[] args) {
		StringBuffer str = new StringBuffer();
		str.append("----- XJCraftSection Command List ------\n");
		for(SubCommand cmd : getCommandHandler().getCommands().values()){
			if(sender.hasPermission(cmd.getPermission())){
				str.append(cmd.toHelp()+"\n");
			}
		}
		sender.sendMessage(str.toString());
		return true;
	}

}
