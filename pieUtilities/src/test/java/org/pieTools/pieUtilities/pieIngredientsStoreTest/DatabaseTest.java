package org.pieTools.pieUtilities.pieIngredientsStoreTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.pieTools.pieUtilities.services.pieIngredientsStore.LoggerPropertiesDAO;
import org.pieTools.pieUtilities.services.pieLogger.PieLogger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Properties;

/**
 * Created by Richard on 09.01.14.
 */
public class DatabaseTest {

	@Before
	public void startUp() {

	}

	@After
	public void cleanUp() {

	}

	@Test
	public void testLoggerPropertiesDAO() {
		String propName = "TestLoggerProperties";
		final AbstractApplicationContext context = new ClassPathXmlApplicationContext("applicationContextTest.xml");
		context.registerShutdownHook();
		LoggerPropertiesDAO loggerPropertiesDAO = context.getBean(LoggerPropertiesDAO.class);

		Properties props = new Properties();

		props.setProperty("log4j.rootLogger", "DEBUG, FILE");
		props.setProperty("log4j.appender.FILE", "org.apache.log4j.FileAppender");
		props.setProperty("log4j.appender.FILE.File", "log.out");
		props.setProperty("log4j.appender.FILE.ImmediateFlush", "true");
		props.setProperty("log4j.appender.FILE.Threshold", "debug");
		props.setProperty("log4j.appender.FILE.Append", "false");
		props.setProperty("log4j.appender.FILE.layout", "org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.FILE.layout.conversionPattern", "%m%n");

		loggerPropertiesDAO.setNewProperty(props, propName);

		LoggerPropertiesDAO loggerPropertiesReader = context.getBean(LoggerPropertiesDAO.class);
		Properties fromDB = loggerPropertiesReader.getPropertyByName(propName);

		Assert.assertEquals(fromDB.getProperty("log4j.rootLogger"), "DEBUG, FILE");

	}



}
