/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pieShare.pieShareServer.services.api.IServer;
import org.pieShare.pieShareServer.services.api.IUserPersistenceService;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class Server implements IServer {

	private IBeanService beanService;
	private SocketListener listener;
	private final ExecutorService executor;

	public Server() {
		executor = Executors.newCachedThreadPool();
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	public void setListener(SocketListener listener) {
		this.listener = listener;
	}

	@Override
	public void start() {
		PieLogger.info(this.getClass(), "Server Started");
		executor.execute(new InputTask());
	}

}
