package org.pieShare.pieTools.pieUtilities.service.pieLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PieLogger
{

	public static final Logger mainLogger = LoggerGetter.getInstance().getLogger();
	//private static Logger mainLogger;

	private Class clazz;

	public PieLogger(Class clazz)
	{
		this.clazz = clazz;
		//mainLogger = LoggerFactory.getLogger(clazz);
		// PropertyConfigurator.configure("log4j.properties");
	}

	public void debug(String message)
	{
		mainLogger.debug(clazz.toString() + " || " + message);
	}

	public void error(String message)
	{
		mainLogger.debug(clazz.toString() + " || " + message);
	}

	public void info(String message)
	{
		mainLogger.debug(clazz.toString() + " || " + message);
	}
}
