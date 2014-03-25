package org.pieShare.pieTools.pieUtilities.service.pieLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PieLogger {

    private static Logger mainLogger;

    public PieLogger(Class clazz) {
        mainLogger = LoggerFactory.getLogger(clazz);
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
