package org.jim.section.util;

import java.io.File;
import java.io.FilenameFilter;

public class YmlFilter implements FilenameFilter {

	@Override
	public boolean accept(File dir, String name) {
		name = name.toLowerCase();
		return name.endsWith("yml")&& !"section.yml".equals(name);
	}

}
