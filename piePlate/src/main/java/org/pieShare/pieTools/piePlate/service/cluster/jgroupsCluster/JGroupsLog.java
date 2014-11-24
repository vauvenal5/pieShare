/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.jgroups.logging.Log;
import org.slf4j.Logger;

/**
 *
 * @author Svetoslav
 */
public class JGroupsLog implements Log {
	
	Logger logger;
	
	public JGroupsLog(Logger logger) {
		this.logger = logger;
	}

	@Override
	public boolean isFatalEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public void fatal(String string) {
		error(string);
	}

	@Override
	public void fatal(String string, Object... os) {
		error(string, os);
	}

	@Override
	public void fatal(String string, Throwable thrwbl) {
		error(string, thrwbl);
	}

	@Override
	public void error(String string) {
		logger.error(string);
	}

	@Override
	public void error(String string, Object... os) {
		logger.error(String.format(string, os));
	}

	@Override
	public void error(String string, Throwable thrwbl) {
		logger.error(string, thrwbl);
	}

	@Override
	public void warn(String string) {
		logger.warn(string);
	}

	@Override
	public void warn(String string, Object... os) {
		logger.warn(String.format(string, os));
	}

	@Override
	public void warn(String string, Throwable thrwbl) {
		logger.warn(string, thrwbl);
	}

	@Override
	public void info(String string) {
		logger.info(string);
	}

	@Override
	public void info(String string, Object... os) {
		logger.info(String.format(string, os));
	}

	@Override
	public void debug(String string) {
		logger.debug(string);
	}

	@Override
	public void debug(String string, Object... os) {
		logger.debug(String.format(string, os));
	}

	@Override
	public void debug(String string, Throwable thrwbl) {
		logger.debug(string, thrwbl);
	}

	@Override
	public void trace(Object o) {
		logger.trace("Object {}", o);
	}

	@Override
	public void trace(String string) {
		logger.trace(string);
	}

	@Override
	public void trace(String string, Object... os) {
		logger.trace(String.format(string, os));
	}

	@Override
	public void trace(String string, Throwable thrwbl) {
		logger.trace(string, thrwbl);
	}

	@Override
	public void setLevel(String string) {
	}

	@Override
	public String getLevel() {
		return "ERROR";
	}
	
}
