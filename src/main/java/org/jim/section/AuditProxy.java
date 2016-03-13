package org.jim.section;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class AuditProxy {

	private Object object;
	private Method applyMethod;
	private boolean init = false;
	private JavaPlugin plugin;

	public AuditProxy(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	private synchronized void init() {
		plugin.getLogger().info("initing xjcraftAuditProxy...");
		try {
			Class.forName("org.jim.bukkit.audit.AuditPlugin");
			Object o = plugin.getServer().getPluginManager().getPlugin("XJCraftAudit");
			Field field = o.getClass().getField("helper");
			field.setAccessible(true);
			object = field.get(o);
			applyMethod = object.getClass().getMethod("isApply", Player.class);
			plugin.getLogger().info("成功接入考核插件");
		} catch (Exception e) {
			object = null;
			plugin.getLogger().info("未发现考核插件: " + e.getMessage());
		}
		init = true;
	}

	public boolean hasPermission(CommandSender p) {
		if (!init)
			init();
		if (!(p instanceof Player))
			return true;
		if (object == null) {
			// 如果没有考核插件则不限制权限
			return true;
		}
		Object result = null;
		try {
			result = applyMethod.invoke(object, (Player) p);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			plugin.getLogger().warning(e.getMessage());
		}
		return result == null ? true : Boolean.valueOf(result.toString());
	}
}
