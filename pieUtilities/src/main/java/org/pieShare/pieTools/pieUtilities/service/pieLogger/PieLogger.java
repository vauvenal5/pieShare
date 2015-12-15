package org.pieShare.pieTools.pieUtilities.service.pieLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PieLogger {
	
	private static Logger getLogger(Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}
	
	public static void info(Class clazz, String message) {
		Logger log = getLogger(clazz);
		log.info(message);
	}
	
	public static void info(Class clazz, String message, Throwable th) {
		Logger log = getLogger(clazz);
		log.info(message, th);
	}
	
	public static void info(Class clazz, String message, Object o) {
		Logger log = getLogger(clazz);
		log.info(message, o);
	}
	
	public static void info(Class clazz, String message, Object... os) {
		Logger log = getLogger(clazz);
		log.info(message, os);
	}
	
	public static void debug(Class clazz, String message) {
		Logger log = getLogger(clazz);
		log.debug(message);
	}
	
	public static void debug(Class clazz, String message, Object... os) {
		Logger log = getLogger(clazz);
		log.debug(message, os);
	}
	
	public static void error(Class clazz, String message) {
		Logger log = getLogger(clazz);
		log.error(message);
	}
	
	public static void error(Class clazz, String message, Throwable thr) {
		Logger log = getLogger(clazz);
		log.error(message, thr);
	}
	
	public static void warn(Class clazz, String message, Object o) {
		Logger log = getLogger(clazz);
		log.warn(message, o);
	}
	
	public static void warn(Class clazz, String message, Throwable thr) {
		Logger log = getLogger(clazz);
		log.warn(message, thr);
	}
	
	public static void trace(Class clazz, String message, Object o) {
		Logger log = getLogger(clazz);
		log.trace(message, o);
	}
	
	public static void trace(Class clazz, String message, Object... o) {
		Logger log = getLogger(clazz);
		log.trace(message, o);
	}
}
