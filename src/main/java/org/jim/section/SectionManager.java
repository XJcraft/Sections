package org.jim.section;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jim.section.util.LeaveQueue;
import org.jim.section.util.Log;
import org.jim.section.util.SectionIO;

public class SectionManager {

	private SectionPlugin plugin;
	private SectionIO sectionIO;
	private Map<String, Section> loads = new HashMap<String, Section>();
	private LeaveQueue leaveQueue;

	private static SectionManager instance = new SectionManager();

	SectionManager() {
	}

	public void init(SectionPlugin plugin) {
		this.plugin = plugin;
		sectionIO = new SectionIO(plugin);
		leaveQueue = new LeaveQueue(new File(plugin.getDataFolder(), "leave-queue.yml"));
		leaveQueue.init();
		loadSections();
	}

	private void loadSections() {
		loads.clear();
		for (Section s : sectionIO.getAllSecs()) {
			if (!s.getPlayingPlayer().isEmpty()) {
				loads.put(s.getName(), s);
			}
		}
	}

	public void destory() {
		for (Section sec : loads.values())
			sectionIO.save(sec);
		loads.clear();
		leaveQueue.save();
	}

	public static SectionManager me() {
		return instance;
	}

	public Section getSection(String name) {
		if (name == null)
			return null;
		Section sec = loads.get(name);
		if (sec == null) {
			sec = sectionIO.getSection(name);
			if (sec != null) {
				loadSection(sec);
			}
		}
		return sec;
	}

	public Section create(String name, Player player) {
		Section section = new Section(this, name);
		section.setCreateTime(System.currentTimeMillis());
		section.setCreatePlayer(player);
		section.setSpawn(player.getLocation());
		sendMsg(player, "section.create.success");
		section.join(player);
		sectionIO.save(section);
		return section;
	}

	public boolean delete(CommandSender sender, Section section) {
		section.leaveall();// leave online
		Iterator<Map.Entry<String, PlayerState>> it = section.getPlayerStates().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, PlayerState> entry = it.next();
			if (entry.getValue().isPlaying()) {
				leaveQueue.add(entry.getKey(), entry.getValue());// offline
				it.remove();
			}
		}
		boolean b = false;
		if (section.getPlayingPlayer().isEmpty()) {
			unloadSection(section, false);
			b = sectionIO.delete(section);
		} else {
			Log.warn("未清除坑: %s", section.getPlayingPlayer());
		}
		return b;
	}

	public Section getPlayingSection(Player player) {
		for (Section sec : loads.values())
			if (sec.isPlaying(player))
				return sec;
		return null;
	}

	public SectionPlugin getPlugin() {
		return plugin;
	}

	public void sendMsg(CommandSender player, String msg, Object... format) {
		String message = plugin.getMsg(msg);
		if (StringUtils.isNotEmpty(message)) {
			player.sendMessage(String.format(message, format));
		}
	}

	public void broadcast(String msg, Object... format) {
		String message = plugin.getMsg(msg);
		if (StringUtils.isNotEmpty(message)) {
			plugin.getServer().broadcastMessage(String.format(message, format));
		}
	}

	public Logger getLogger() {
		return plugin.getLogger();
	}

	public List<Section> getAllSections() {
		return sectionIO.getAllSecs();
	}

	public LeaveQueue getLeaveQueue() {
		return leaveQueue;
	}

	public void loadSection(Section section) {
		if (!loads.containsKey(section.getName()))
			loads.put(section.getName(), section);
	}

	public void unloadSection(Section section, boolean save) {
		if (save)
			sectionIO.save(section);
		if (loads.containsKey(section.getName())) {
			loads.remove(section.getName());
		}
	}

	public boolean isLoad(Section section) {
		return loads.containsKey(section.getName());
	}

}
