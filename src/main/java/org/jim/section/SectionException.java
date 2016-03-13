package org.jim.section;

public class SectionException extends RuntimeException {

	public SectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public SectionException(String message) {
		super(message);
	}

	public static void throwIt(String message){
		throw new SectionException(message);
	}
	public static void throwIt(String message, Throwable cause){
		throw new SectionException(message, cause);
	}
}
