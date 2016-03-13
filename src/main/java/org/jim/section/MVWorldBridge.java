package org.jim.section;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jim.section.util.Log;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class MVWorldBridge {

	private MVWorldManager proxy;
	private boolean isValid = false;
	private static MVWorldBridge instance;

	MVWorldBridge(JavaPlugin plugin) {
		Plugin p = plugin.getServer().getPluginManager()
				.getPlugin("Multiverse-Core");
		instance = this;
		if (p == null) {
			plugin.getLogger().warning("未接入Multiverse-Core");
		} else {
			proxy = ((MultiverseCore) p).getMVWorldManager();
			isValid = true;
			plugin.getLogger().warning("成功接入Multiverse-Core: "+proxy);
		}
	}

	public static MVWorldBridge me() {
		return instance;
	}

	public boolean isValid() {
		return isValid && (proxy != null);
	}

/*	public boolean isMVWorld(String world) {
		return isValid() ? proxy.isMVWorld(world) : false;
	}*/

	public boolean unloadWorld(String world) {
		Log.info("unloading world %s", world);
		return isValid() ? proxy.unloadWorld(world) : false;
	}

	public boolean loadWorld(String world) {
		Log.info("loading world %s", world);
		return isValid() ? proxy.loadWorld(world) : false;
	}
}
