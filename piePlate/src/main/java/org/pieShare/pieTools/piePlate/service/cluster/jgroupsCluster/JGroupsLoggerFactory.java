/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.service.cluster.jgroupsCluster;

import org.jgroups.logging.CustomLogFactory;
import org.jgroups.logging.Log;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Svetoslav
 */
public class JGroupsLoggerFactory implements CustomLogFactory{

	@Override
	public Log getLog(Class type) {
		return new JGroupsLog(LoggerFactory.getLogger(type));
	}

	@Override
	public Log getLog(String string) {
		return new JGroupsLog(LoggerFactory.getLogger(string));
	}
	
}
