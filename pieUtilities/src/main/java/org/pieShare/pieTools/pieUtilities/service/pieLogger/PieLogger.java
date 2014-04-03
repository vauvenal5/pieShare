package org.pieShare.pieTools.pieUtilities.service.pieLogger;

import org.slf4j.Logger;

public class PieLogger  {

    public static final Logger mainLogger = LoggerGetter.getInstance().getLogger();
    
    
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
