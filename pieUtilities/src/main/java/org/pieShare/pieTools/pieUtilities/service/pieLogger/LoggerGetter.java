/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.pieLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Richard
 */
public class LoggerGetter {

	private static final Logger mainLogger = LoggerFactory.getLogger(PieLogger.class);
	private static LoggerGetter instance;

	private LoggerGetter() {
	}

	public static LoggerGetter getInstance() {
		if (instance == null) {
			instance = new LoggerGetter();
		}

		return instance;
	}

	public Logger getLogger() {
		return mainLogger;
	}

}
