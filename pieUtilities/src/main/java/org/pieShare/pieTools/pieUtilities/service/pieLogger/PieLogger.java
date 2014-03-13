

package org.pieShare.pieTools.pieUtilities.service.pieLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PieLogger {

	private static Logger mainLogger = LoggerFactory.getLogger(PieLogger.class);

	public void debug(Class clazz, String message) {
		if (mainLogger.isDebugEnabled()) {
			Logger logger = LoggerFactory.getLogger(clazz);
			logger.debug(message);
		}
	}

	public void error(Class clazz, String message) {
		if (mainLogger.isErrorEnabled()) {
			Logger logger = LoggerFactory.getLogger(clazz);
			logger.debug(message);
		}
	}

	public void info(Class clazz, String message) {
		if (mainLogger.isInfoEnabled()) {
			Logger logger = LoggerFactory.getLogger(clazz);
			logger.info(message);
		}
	}
}
