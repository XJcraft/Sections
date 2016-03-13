package org.jim.section.util;

import java.util.regex.Pattern;

public class BoolUtility {
	
	private static Pattern BOOLPAT = Pattern.compile("(t|f|true|false)"); 

	public static Boolean parse(String str){
		if(str == null) return null;
		str = str.toLowerCase();
		if(!BOOLPAT.matcher(str).find())
			return null;
		return "t".equals(str)|| "true".equals(str);
	}

}
