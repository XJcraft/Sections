package org.jim.section;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jim.section.cmds.CommandHandler;
import org.yaml.snakeyaml.Yaml;

public class SectionPlugin extends JavaPlugin {

	private Map<String,Object> msg = new HashMap<>();
	public static SectionPlugin instance;

	@Override
	public void onEnable() {
		super.onEnable();
		instance= this;
		saveResource("configmsg.yml", false);
		try {
			loadMsgConfig();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		SectionManager.me().init(this);
		new MVWorldBridge(this);
		getServer().getPluginManager().registerEvents(new Events(), this);
		CommandHandler handler = new CommandHandler(this);
		getCommand("sec").setExecutor(handler);
		getCommand("section").setExecutor(handler);
	}

	private void loadMsgConfig() throws IOException,
			InvalidConfigurationException {
		Yaml y = new Yaml();
		this.msg.putAll((Map)y.load(getResource("configmsg.yml")));
		File msg = new File(getDataFolder(), "configmsg.yml");
		if (msg.exists()) {
			//msgYaml.load(msg);
			 this.msg.putAll((Map)y.load(new FileInputStream(msg)));
		}
	}

	@Override
	public void reloadConfig() {
		super.reloadConfig();
		try {
			loadMsgConfig();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		SectionManager.me().destory();
		super.onDisable();
	}

	public String getMsg(String str) {
		String s =(String) msg.get(str);
		if (s != null) {
			s = s.replaceAll("&", "\u00A7");
		} else
			getLogger().warning("message: " + str + "不存在");
		return s;
	}
}
