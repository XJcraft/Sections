package org.jim.section;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.MemorySection;
import org.jim.section.util.Log;

public class LocationRef {

	private String world;
	private double x;
	private double y;
	private double z;

	private Location ref;

	public LocationRef(String world, double x, double y, double z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public LocationRef(Location location) {
		this(location.getWorld().getName(), location.getX(), location.getY(),
				location.getZ());
	}

	public String getWorld() {
		return world;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	
	public Location getRef() {
		World w = Bukkit.getWorld(world);
		if (w == null) {
			Log.warn("world %s isnot exist!", world);
			return null;
		}
		ref = new Location(w, x, y, z);
		return ref;
	}

	public void serial(MemorySection yaml, String prefix) {
		yaml.set(prefix + ".world", world);
		yaml.set(prefix + ".x", x);
		yaml.set(prefix + ".y", y);
		yaml.set(prefix + ".z", z);
	}

	public static LocationRef unSerial(MemorySection yaml, String prefix) {
		String worldName = (String) yaml.get(prefix + ".world");
		if (worldName == null) {
			return null;
		}
		int x = yaml.getInt(prefix + ".x");
		int y = yaml.getInt(prefix + ".y");
		int z = yaml.getInt(prefix + ".z");
		return new LocationRef(worldName, x, y, z);
	}

}
