package org.jim.section.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.jim.section.PlayerState;

public class LeaveQueue extends Storeable {

	private Map<String, PlayerState> leaveQueue = new HashMap<>();
	private File conf;

	public LeaveQueue(File data) {
		this.conf = data;
	}

	@Override
	public MemorySection serial(MemorySection yaml) {
		for (Map.Entry<String, PlayerState> e : leaveQueue.entrySet()) {
			yaml.set(e.getKey(), e.getValue().serial(new MemoryConfiguration()));
		}
		return yaml;
	}

	public void add(OfflinePlayer player, PlayerState state) {
		leaveQueue.put(player.getName(), state);
	}
	public void add(String player, PlayerState state) {
		leaveQueue.put(player, state);
	}

	public PlayerState get(OfflinePlayer player) {
		return leaveQueue.get(player.getName());
	}

	public void remove(OfflinePlayer player) {
		leaveQueue.remove(player.getName());
	}

	@Override
	public void unSerial(MemorySection yaml) {
		for (String p : yaml.getKeys(false)) {
			MemorySection s = (MemorySection) yaml.get(p);
			PlayerState state = new PlayerState(null);
			state.unSerial(s);
			leaveQueue.put(p, state);
		}
	}

	public void init() {
		if (conf.exists()) {
			try {
				load(conf);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void save() {
		try {
			save(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
