package org.jim.section.util;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jim.section.SectionManager;

public class Log {

	public static Logger getLogger(){
		return SectionManager.me().getPlugin().getLogger();
		//Logger.getGlobal().setLevel(Level.INFO);
		//return Logger.getGlobal();
	}
	
	public static void info(String msg,Object...os){
		getLogger().info(String.format(msg,os));
	}
	public static void warn(String msg,Object...os){
		getLogger().warning(String.format(msg,os));
	}
	public static void error(String msg,Throwable ex){
		getLogger().log(Level.SEVERE, msg, ex);
	}

	public static void throwException(String msg){
		throw new RuntimeException(msg);
	}
}
