package org.pieShare.pieTools.pieUtilities.service.pieLogger;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PieLogger  {

    private static final Logger mainLogger = LoggerFactory.getLogger(PieLogger.class);

    
    
    public PieLogger(Class clazz) {
        //mainLogger = LoggerFactory.getLogger(clazz);
	// PropertyConfigurator.configure("log4j.properties");
    }

    public void debug(String message) {
        mainLogger.debug(message);
    }

    public void error(String message) {
        mainLogger.error(message);
    }

    public void info(String message) {
        mainLogger.info(message);
    }
}
