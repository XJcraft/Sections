package org.jim.section.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jim.section.SectionManager;
import org.jim.section.Section;
import org.jim.section.SectionPlugin;

public class SectionIO {

	private SectionPlugin plugin;
	private File sectionFolder;
	private Map<String, Section> cache = new HashMap<String, Section>();

	public SectionIO(SectionPlugin plugin) {
		super();
		this.plugin = plugin;
		this.sectionFolder = new File(plugin.getDataFolder(), "section");
		if (!sectionFolder.exists())
			sectionFolder.mkdirs();
		loadCache();

	}

	protected void loadCache() {
		for (Section s : _getAllSec()) {
				cache.put(s.getName(), s);
		}
	}

	protected List<Section> _getAllSec() {
		List<Section> ss = new ArrayList<>();
		for (String f : sectionFolder.list(new YmlFilter())) {
			Section s = _getSection(f.substring(0, f.indexOf(".ym")));
			if (s != null)
				ss.add(s);
		}
		return ss;
	}

	protected Section _getSection(String name) {
		plugin.getLogger().info("Loading section..." + name);
		Section s = new Section(SectionManager.me(), name);
		try {
			s.load(new File(sectionFolder, name + ".yml"));
			return s;
		} catch (Exception e) {
			Log.error("load Section " + name + " fail.", e);
		}
		return null;
	}

	public void save(Section sec) {
		plugin.getLogger().info("Saving section..." + sec.getName());
		File f = new File(sectionFolder, sec.getName() + ".yml");
		try {
			sec.save(f);
			cache.put(sec.getName(), sec);
		} catch (IOException e) {
			Log.error("save Section " + sec.getName() + " fail.", e);
		}
	}

	public boolean delete(Section sec) {
		// cache remove
		cache.remove(sec.getName());
		// file remove
		File f = new File(sectionFolder, sec.getName() + ".yml");
		return f.delete();
	}

	public Section getSection(String name) {
		return cache.get(name);
	}

	public List<Section> getAllSecs() {
		return new ArrayList<>(cache.values());
	}

	public boolean delete(File file) {
		boolean flag = false;
		if (file.isFile()) {
			flag = file.delete();
		} else {
			for (File f : file.listFiles()) {
				if (!delete(f)) {
					plugin.getLogger().warning("delete " + f + " fail");
				}

			}
			flag = file.delete();
		}
		return flag;
	}

}
