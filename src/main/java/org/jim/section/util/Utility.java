package org.jim.section.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.bukkit.Location;
import org.bukkit.World;

public class Utility {

	public static <T> T get(T[] array, int index) {
		if (array != null && index >= 0 && index < array.length)
			return array[index];
		return null;
	}

	public static boolean equalWorld(World w1, World w2) {
		if (w1 == null || w2 == null)
			return false;
		return w1.getName().equals(w2.getName());
	}

	public static boolean equalWorld(Location loc, World w2) {
		if (loc == null)
			return false;
		return equalWorld(loc.getWorld(), w2);
	}

	public static boolean equalLocation(Location loc, Location loc2) {
		if (loc == null || loc2 == null)
			return false;
		return equalWorld(loc, loc2.getWorld())
				&& loc.getBlockX() == loc2.getBlockX()
				&& loc.getBlockY() == loc2.getBlockY()
				&& loc.getBlockZ() == loc2.getBlockZ();
	}

	public static String md5(String str) {
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			byte[] arrayOfByte = localMessageDigest.digest(str.getBytes("UTF-8"));
			BigInteger localBigInteger = new BigInteger(1, arrayOfByte);
			return localBigInteger.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String base64Encode(String paramString) {
		if (paramString == null) {
			return "";
		}
		return DatatypeConverter.printBase64Binary(paramString.getBytes());
	}

	public static String base64Decode(String paramString) {
		if (paramString.isEmpty()) {
			return "";
		}
		byte[] arrayOfByte = DatatypeConverter.parseBase64Binary(paramString);
		return new String(arrayOfByte);
	}

	public static String join(Collection<String> list,String split){
		StringBuffer sb = new StringBuffer();
		for(String s : list){
			sb.append(split+s);
		}
		if(sb.length()>0)
			return sb.substring(split.length());
		return sb.toString();
	}
	
	public static void main(String[] args) {
		List<String> ab = new ArrayList<String>();
		ab.add("faf");
		ab.add("faf");
		System.out.println(join(ab, ","));
	}
}
