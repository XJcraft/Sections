package org.jim.section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.jim.section.util.Log;
import org.jim.section.util.Storeable;
import org.jim.section.util.Title;
import org.jim.section.util.Utility;
import org.jim.section.yml.StatusToggle;

public class Section extends Storeable {

	public static final String[] FLAGS = { "teammode", "join", "leave", "save", "exit", "tp", "autoworld", "board" };
	private String name;
	private long createTime;
	private LocationRef spawn;
	private String createPlayer;

	private Map<String, Boolean> flags = new HashMap<String, Boolean>();
	private Map<String, PlayerState> players = Collections.synchronizedMap(new HashMap<String, PlayerState>());
	private Rightboard board;

	private SectionManager manager;
	private StatusToggle statusToggle;

	public Section(SectionManager manager, String name) {
		this();
		this.manager = manager;
		this.name = name;
		board = new Rightboard(this);
	}

	private Section() {
		super();
		statusToggle = new StatusToggle(this);
		for (String flag : FLAGS)
			flags.put(flag.toLowerCase(), true);
	}

	public boolean isCreater(Player player) {
		return createPlayer.equalsIgnoreCase(player.getName());
	}

	public void updateBoard() {
		if (getFlag("board"))
			board.update();
	}

	public Location getLastLocation(Player player) {
		PlayerState state = getPlayerState(player);
		Location spawn = getSpawn();
		if (state != null && state.getTo() != null) {
			Location loc = state.getTo();
			if (Utility.equalWorld(spawn, loc.getWorld())) // 保证不超出世界
				spawn = loc;
		}
		return spawn;
	}

	public boolean isLoadWorld() {
		return spawn.getRef() != null;
	}

	public boolean join(Player player) {
		if (check(player, "join")) {
			if (isPlaying(player)) {
				SectionException.throwIt(String.format("玩家%s不在坑（%s）", player.getName(), getName()));
			}
			PlayerState state = getPlayerState(player);
			if (state == null) {
				state = new PlayerState(this);
				state.setJoinTime(System.currentTimeMillis());
				addPlayerState(player, state);
			}
			loadState(player, true);
			sentTitle(player, getName(), ChatColor.AQUA + "你加入了坑。");
			broadcastMsg("§6%s: §b%s§f加入了坑。", getName(), player.getName());
			manager.loadSection(this);
			return true;
		}
		return false;
	}

	private void sentTitle(Player player, String title, String subtitle) {
		try {
			new Title(title, title).send(player);
		} catch (Exception e) {
			SectionPlugin.instance.getLogger().warning("Title send error: " + e.getLocalizedMessage());
		}
	}

	// to-->from
	protected void saveState(Player player, boolean tp) {
		PlayerState state = getPlayerState(player);
		if (state != null) {
			state.setTo(player.getLocation());
			if (tp) {
				Location fromLocation = state.getFrom();
				if (fromLocation == null) {
					SectionException.throwIt("Miss location on join place.");
				}
				// 防止床出生点的意外:离开坑时，床出生点还保留在坑内，则强制设置坑外出生点
				if (player.getBedSpawnLocation() != null) {
					state.setSave(player.getBedSpawnLocation());
				}
				player.setBedSpawnLocation(state.getFromBed(), true);

				player.teleport(state.getFrom());
				state.setLeaveTime(System.currentTimeMillis());
				state.setPlaying(false);
				board.remove(player);
				statusToggle.savePlayer(player);
			}
			updateBoard();
		}
	}

	// from-->to
	protected void loadState(Player player, boolean tp) {
		PlayerState state = getPlayerState(player);
		if (state != null) {
			state.setFrom(player.getLocation());
			if (tp) {
				// tp spawn location
				Location spawn = getLastLocation(player);
				if (spawn == null) {
					SectionException.throwIt(String.format("%s: 出生点丢失！", getName()));
				}
				Log.info("%s tp to %s", player, spawn);
				if (!Utility.equalLocation(player.getLocation(), spawn))
					player.teleport(spawn);
				player.getWorld().playEffect(player.getLocation(), Effect.ENDER_SIGNAL, null);
				player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, .3f, .5f);
				state.setPlaying(true);
				// save bed
				Location bed = player.getBedSpawnLocation();
				if (bed != null) {
					state.setFromBed(bed);
				}
				// load section savebed
				player.setBedSpawnLocation(state.getSave(), true);
				statusToggle.loadPlayer(player);
			}
			updateBoard();
		}
	}

	protected void removeState(OfflinePlayer player) {
		removeState(player.getName());
	}

	protected void removeState(String player) {
		players.remove(player);
		updateBoard();
		statusToggle.remove(player);
	}

	public Location getPlayerSpawn(Player player) {
		Location loc = getSpawn();
		Location save = player.getBedSpawnLocation();
		if (loc == null)
			SectionPlugin.instance.getLogger().warning(String.format("坑%s失败出生点了！！", getName()));
		return save == null ? loc : save;
	}

	public boolean save(Player player) {
		if (!isPlaying(player)) {
			Log.warn("Player %s is not playing in section %s.", player.getName(), this.getName());
			return false;
		}
		player.setBedSpawnLocation(player.getLocation(), true);
		return true;
	}

	public boolean leave(Player player) {
		if (check(player, "leave")) {
			if (!isPlaying(player)) {
				SectionException.throwIt(String.format("%s 不在坑里。", player.getName()));
			}
			saveState(player, true);
			broadcastMsg("§6%s: §b%s§f离开了坑。", getName(), player.getName());
			unload();
			unloadWorld();
		}
		return false;
	}

	// 没有玩家玩则卸载
	private void unload() {
		boolean unload = true;
		for (PlayerState state : players.values())
			if (state.isPlaying()) {
				unload = false;
				break;
			}
		if (unload)
			manager.unloadSection(this, true);
	}

	// 延迟卸载 5min
	private void unloadWorld() {
		if (!MVWorldBridge.me().isValid())
			return;
		if (getFlag("autoworld"))
			Bukkit.getScheduler().runTaskLaterAsynchronously(SectionPlugin.instance, new Runnable() {

				@Override
				public void run() {
					Location loc = spawn.getRef();
					if (loc != null && loc.getWorld().getPlayers().isEmpty()) {
						broadcastMsg("§6%s: §f世界§b %s §f人数为 0 , 卸载中...", getName(), spawn.getWorld());
						if (MVWorldBridge.me().unloadWorld(spawn.getWorld())) {
							broadcastMsg("§6%s: §f世界§b %s §f卸载成功！", getName(), spawn.getWorld());
						} else {
							broadcastMsg("§6%s: §f世界§b %s §c卸载失败！", getName(), spawn.getWorld());
						}
					}
				}
			}, 20 * 60 * 5);
	}

	public void leaveall() {
		for (OfflinePlayer player : getPlayingPlayer()) {
			if (player.isOnline()) {
				Player p = player.getPlayer();
				saveState(p, true);
				broadcastMsg("§6%s: §b%s§f离开了坑。", getName(), player.getName());
			}
		}
		unload();
		unloadWorld();
	}

	public void loadall() {
		for (OfflinePlayer player : getPlayingPlayer()) {
			if (player.isOnline()) {
				Player p = player.getPlayer();
				if (isPlaying(player)) {
					loadState(p, false);
				} else {
					loadState(p, true);
				}
				broadcast("section.join.success", player.getName(), getName());
			}
		}

	}

	public boolean exit(Player player) {
		if (check(player, "exit")) {
			if (isPlaying(player)) {
				leave(player);
			}
			broadcast("section.exit.success", player.getName(), getName());
			removeState(player);
			manager.loadSection(this);
			return true;
		}
		return false;
	}

	public boolean hasJoin(Player player) {
		return getPlayerState(player) != null;
	}

	public PlayerState getPlayerState(OfflinePlayer player) {
		return players.get(player.getName());
	}

	public void addPlayerState(OfflinePlayer player, PlayerState state) {
		players.put(player.getName(), state);
	}

	public boolean isPlaying(OfflinePlayer player) {
		PlayerState state = getPlayerState(player);
		return state == null ? false : state.isPlaying();
	}

	public String getName() {
		return name;
	}

	public long getCreateTime() {
		return createTime;
	}

	public String getCreatePlayer() {
		return createPlayer;
	}

	public boolean setFlag(String key, boolean value) {
		if (!ArrayUtils.contains(FLAGS, key.toLowerCase()))
			return false;
		flags.put(key, value);
		manager.loadSection(this);
		return true;
	}

	public boolean getFlag(String key) {
		Boolean b = flags.get(key.toLowerCase());
		return b == null ? false : b;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public void setCreatePlayer(Player createPlayer) {
		this.createPlayer = createPlayer.getName();
	}

	public List<Player> getOnlinePlayer() {
		List<Player> ps = new ArrayList<>();
		for (String p : players.keySet()) {
			Player player = Bukkit.getPlayerExact(p);
			if (p != null)
				ps.add(player);

		}
		return ps;
	}

	public List<OfflinePlayer> getAllPlayer() {
		List<OfflinePlayer> ps = new ArrayList<>(players.size());
		for (String p : players.keySet()) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(p);
			if (player != null)
				ps.add(player);

		}
		return ps;
	}

	public List<OfflinePlayer> getPlayingPlayer() {
		List<OfflinePlayer> ps = new ArrayList<>();
		for (Map.Entry<String, PlayerState> e : players.entrySet()) {
			if (e.getValue().isPlaying())
				ps.add(Bukkit.getOfflinePlayer(e.getKey()));

		}
		return ps;
	}

	public Map<String, PlayerState> getPlayerStates() {
		return players;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Section other = (Section) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public Location getSpawn() {
		return getRef(spawn);
	}

	public void setSpawn(Location spawn) {
		this.spawn = new LocationRef(spawn);
	}

	@Override
	public MemorySection serial(MemorySection yaml) {
		yaml.set("name", name);
		yaml.set("createTime", createTime);
		yaml.set("createPlayer", createPlayer);
		// spawn
		// CommonUtility.putLocation(yaml, spawn, "spawn");
		spawn.serial(yaml, "spawn");
		// flag
		for (String key : flags.keySet())
			yaml.set("flag." + key, flags.get(key));
		for (Map.Entry<String, PlayerState> e : players.entrySet()) {
			yaml.set("players." + e.getKey(), e.getValue().serial(new MemoryConfiguration()));
		}
		return yaml;
	}

	public boolean tp(Player player, LocationRef ref) {
		Location loc = getRef(ref);
		if (loc != null) {
			return player.teleport(loc);
		} else {
			return false;
		}
	}

	private Location getRef(LocationRef ref) {
		Location loc = ref.getRef();
		if (loc == null && getFlag("autoworld")) {
			broadcastMsg("§6%s: §f世界§b %s §f加载中...", getName(), ref.getWorld());
			if (MVWorldBridge.me().loadWorld(ref.getWorld())) {
				loc = ref.getRef();
				broadcastMsg("§6%s: §f世界 §b %s §f加载成功!", getName(), ref.getWorld());
			} else {
				broadcastMsg("§6%s: §f世界 §b %s §c加载失败!", getName(), ref.getWorld());
			}
		}
		return loc;
	}

	@Override
	public void unSerial(MemorySection yaml) {
		this.name = yaml.getString("name");
		this.createTime = yaml.getLong("createTime");
		this.createPlayer = yaml.getString("createPlayer");
		this.spawn = LocationRef.unSerial(yaml, "spawn");
		for (String key : FLAGS) {
			String lower = key.toLowerCase();
			flags.put(lower, yaml.getBoolean("flag." + lower));
		}
		MemorySection ms = (MemorySection) yaml.get("players");
		if (ms != null)
			for (String p : ms.getKeys(false)) {
				MemorySection s = (MemorySection) yaml.get("players." + p);
				PlayerState state = new PlayerState(this);
				state.unSerial(s);
				// System.out.println("----"+state);
				players.put(p, state);
			}
		Validate.notNull(name, "name 不能空");
		Validate.notNull(createTime, "createTime 不能空");
		Validate.notNull(createPlayer, "createPlayer 不能空");
		Validate.notNull(spawn, "spawn 不能空");
	}

	@Override
	public String toString() {
		return "Section [name=" + name + ", createTime=" + createTime + ", spawn=" + spawn + ", createPlayer="
				+ createPlayer + ", flags=" + flags + ", playerStates=" + players + ", playingPlayer=" + players + "]";
	}

	public void broadcast(String msg, String... format) {
		for (String p : players.keySet()) {
			Player player = Bukkit.getPlayerExact(p);
			if (player != null)
				SectionManager.me().sendMsg(player, msg, format);
		}
	}

	public void broadcastMsg(String msg, String... format) {
		for (String p : players.keySet()) {
			Player player = Bukkit.getPlayerExact(p);
			if (player != null)
				player.sendMessage(String.format(msg, format));
		}
	}

	public boolean check(Player player, String flag) {
		boolean f = getFlag(flag);
		Log.info("%s: %s.%s-->%s", player.getName(), getName(), flag, f);
		if (!f) {
			// f is false
			player.sendMessage("§cYou dont have permission to user this command!");
		}
		return f;
	}

	public Rightboard getBoard() {
		return board;
	}

}
