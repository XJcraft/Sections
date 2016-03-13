package org.jim.section;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jim.section.util.LegacyMergeSort;

public class Rightboard {

	private Section section;
	private Scoreboard scoreboard;
	private Objective objective;

	public Rightboard(Section section) {
		super();
		this.section = section;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		objective = scoreboard.registerNewObjective("objective", "dummy");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.setDisplayName(ChatColor.YELLOW + section.getName());
	}

	public void update() {
		reset();
		int i = 3;
		int current = 0;
		List<Item> items = getItems();
		for (Item item : items) {
			if (item.online && item.playing) {
				Player rel = item.player.getPlayer();
				Scoreboard b = rel.getScoreboard();
				if (!scoreboard.equals(b))
					rel.setScoreboard(scoreboard);
				current++;
				setScore(ChatColor.GREEN + rel.getName(), i);
			} else {
				setScore(ChatColor.GRAY + (item.online ? "" : "-") + item.player.getName(), i);
			}
			i++;
			if (i > 15)
				break;
		}
		setScore("\t总人数: " + items.size(), 1);
		setScore("\t活跃人数: " + current, 2);
	}

	private List<Item> getItems() {
		List<Item> items = new ArrayList<Item>();
		for (OfflinePlayer player : section.getAllPlayer()) {
			Item item = new Item();
			item.online = player.isOnline();
			item.playing = section.isPlaying(player);
			item.player = player;
			items.add(item);
		}
		LegacyMergeSort.sort(items);
		return items;
	}

	private void reset() {
		for (String s : scoreboard.getEntries()) {
			scoreboard.resetScores(s);
		}
	}

	public void remove(Player player) {
		if (player.getScoreboard() == scoreboard)
			player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
	}

	private void setScore(String name, int score) {
		if (name.length() > 16)
			name = name.substring(0, 12) + "...";
		objective.getScore(name).setScore(score);
	}

	class Item implements Comparable<Item> {
		boolean online;
		boolean playing;
		OfflinePlayer player;

		@Override
		public int compareTo(Item o) {
			if (online && playing)
				return 2;
			if (online)
				return 1; // no playing
			return player.getName().compareTo(o.player.getName());
		}

	}
}
