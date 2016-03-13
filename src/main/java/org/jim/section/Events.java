package org.jim.section;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jim.section.util.LeaveQueue;
import org.jim.section.util.Log;

public class Events implements Listener {
	/**
	 * 坑里的人要在坑里重生
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onRespawn(PlayerRespawnEvent event) {
		Section sec = SectionManager.me().getPlayingSection(event.getPlayer());
		if (sec != null) {
			Location spawn = sec.getPlayerSpawn(event.getPlayer());
			if (spawn == null) {
				Log.warn("Location(%s->%s) lost", sec.getName(), event.getPlayer());
				event.getPlayer().sendMessage("§c卧槽！这坑的出生点没了？！");
				return;
			}
			event.setRespawnLocation(spawn);
		}
	}

	/**
	 * 坑里teammode下无法攻击对方
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if ((event.getEntity() instanceof Player)) {
			// damager
			Player damager = null;
			if (event.getDamager() instanceof Player)
				damager = (Player) event.getDamager();
			else if (event.getDamager() instanceof Projectile) {
				Projectile tile = (Projectile) event.getDamager();
				if (tile.getShooter() instanceof Player) {
					damager = (Player) tile.getShooter();
				}
			}
			// damagee
			Player damagee = (Player) event.getEntity();
			if (damager != null && damagee != null) {
				Section sec = SectionManager.me().getPlayingSection(damager);
				if (sec != null && sec.getFlag("teamMode") && sec.isPlaying(damagee))
					event.setCancelled(true);
			}
		}

	}

	/**
	 * 坑里睡床，保存一下
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBenEnter(PlayerBedEnterEvent event) {
		Player p = event.getPlayer();
		Section sec = SectionManager.me().getPlayingSection(p);
		if (sec != null) {
			sec.save(p);
			p.sendMessage(String.format("§6%s: §f出生点保存成功！", sec.getName()));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		final Section sec = SectionManager.me().getPlayingSection(p);
		if (sec != null) {
			// 延时更新面板
			Bukkit.getScheduler().runTaskLater(SectionPlugin.instance, new Runnable() {

				@Override
				public void run() {
					sec.updateBoard();
				}
			}, 20 * 2);
		}
	}

	@EventHandler
	public void autoLeave(PlayerJoinEvent event) {
		LeaveQueue queue = SectionManager.me().getLeaveQueue();
		PlayerState state = queue.get(event.getPlayer());
		leaveState(event.getPlayer(), state, 0);
	}

	private void leaveState(final Player player, final PlayerState state, final int count) {
		if (state != null && state.isPlaying()) {
			if (state.getFrom() == null) {
				player.sendMessage(ChatColor.RED + "离开坑失败！坑前位置丢失！");
				Log.info(player.getName() + " 离开坑失败！坑前位置丢失！");
				return;
			}
			if (player.teleport(state.getFrom())) {
				SectionManager.me().getLeaveQueue().remove(player);
				Log.info("Player %s leave from leaveQueue-->%s", player.getName(), true);
			} else {
				Log.warn("Player %s leave from leaveQueue-->%s", player.getName(), false);
				if (count > 10) {
					player.sendMessage(ChatColor.RED + "无法退坑...请与op联系");
					return;
				}
				player.sendMessage(ChatColor.RED + "退坑失败...15秒后重新尝试");
				player.setNoDamageTicks(20 * 15);
				Bukkit.getScheduler().runTaskLater(SectionPlugin.instance, new Runnable() {

					@Override
					public void run() {
						leaveState(player, state, count + 1);

					}
				}, 20 * 14);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onJoin2(final PlayerJoinEvent event) {
		final Section sec = SectionManager.me().getPlayingSection(event.getPlayer());
		if (sec == null)
			return;
		if (!sec.isLoadWorld() && sec.getFlag("autoworld")) {
			final Player player = event.getPlayer();
			final int damageTick = player.getNoDamageTicks();
			player.setNoDamageTicks(20 * 60);
			Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(SectionPlugin.instance, new Runnable() {

				@Override
				public void run() {
					try {
						Location last = sec.getLastLocation(event.getPlayer());
						if (last != null) {
							event.getPlayer().teleport(last);
						}
					} catch (Exception e) {
						Log.error("teleport error: " + event.getPlayer(), e);
					} finally {
						player.setNoDamageTicks(damageTick);
					}
				}
			}, 0);
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		final Section sec = SectionManager.me().getPlayingSection(p);
		if (sec != null) {
			Bukkit.getScheduler().runTaskLater(SectionPlugin.instance, new Runnable() {

				@Override
				public void run() {
					sec.updateBoard();
				}
			}, 20 * 2);
		}
	}
}
