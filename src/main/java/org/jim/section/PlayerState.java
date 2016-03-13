package org.jim.section;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.jim.section.util.Storeable;
import org.jim.section.util.Utility;

public class PlayerState extends Storeable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1300093115771021629L;
	private LocationRef from;
	private LocationRef fromBed;
	private LocationRef to;
	private LocationRef save;
	private long joinTime;
	private long leaveTime;
	private boolean isPlaying = false;
	
	private Section section;

	public PlayerState(Section section) {
		this.section =section;
	}

	public Location getFrom() {
		return from == null ? null : from.getRef();
	}

	public Location getTo() {
		return to == null ? null : to.getRef();
	}

	public long getJoinTime() {
		return joinTime;
	}

	public long getLeaveTime() {
		return leaveTime;
	}

	public void setFrom(Location from) {
		this.from = new LocationRef(from);
	}

	public void setTo(Location to) {
		if(Utility.equalWorld( section.getSpawn(),to.getWorld()))
			this.to = new LocationRef(to);
	}

	public void setJoinTime(long joinTime) {
		this.joinTime = joinTime;
	}

	public void setLeaveTime(long leaveTime) {
		this.leaveTime = leaveTime;
	}

	public Location getSave() {
		return save == null ? null : save.getRef();
	}

	public void setSave(Location to) {
		this.save = new LocationRef(to);
	}

	@Override
	public MemorySection serial(MemorySection yaml) {
		yaml.set("joinTime", joinTime);
		yaml.set("leaveTime", leaveTime);
		yaml.set("playing", isPlaying);
		// if(from!=null)
		from.serial(yaml, "from");
		if (to != null)
			to.serial(yaml, "to");
		if (save != null) {
			save.serial(yaml, "save");
		}
		if(fromBed!=null){
			fromBed.serial(yaml, "bed");
		}
		return yaml;
	}

	@Override
	public void unSerial(MemorySection yaml) {
		this.joinTime = yaml.getLong("joinTime");
		this.leaveTime = yaml.getLong("leaveTime");
		this.isPlaying = yaml.getBoolean("playing", false);
		this.from = LocationRef.unSerial(yaml, "from");
		this.to = LocationRef.unSerial(yaml, "to");
		this.save = LocationRef.unSerial(yaml, "save");
		this.fromBed = LocationRef.unSerial(yaml, "bed");
		Validate.notNull(joinTime, "joinTime 不能空");
		Validate.notNull(from, "from 不能空");
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	@Override
	public String toString() {
		return "PlayerState [from=" + from + ", to=" + to + ", joinTime="
				+ joinTime + ", leaveTime=" + leaveTime + "]";
	}

	public Location getFromBed() {
		return fromBed == null? null: fromBed.getRef();
	}

	public void setFromBed(Location bed) {
		this.fromBed = new LocationRef(bed);
	}

}
