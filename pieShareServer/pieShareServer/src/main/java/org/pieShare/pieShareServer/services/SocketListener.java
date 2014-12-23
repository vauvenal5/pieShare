/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.pieShare.pieShareServer.services.api.IIncomeTask;
import org.pieShare.pieShareServer.services.api.ISocketListener;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author Richard
 */
public class SocketListener implements ISocketListener {

	private int port;
	private IBeanService beanService;
	private final ExecutorService executor;

	public SocketListener() {
		executor = Executors.newCachedThreadPool();
	}

	public void setBeanService(IBeanService beanService) {
		this.beanService = beanService;
	}

	@Override
	public void run() {
		port = 6312;
		PieLogger.info(this.getClass(), "Listener Running");
		ServerSocket server;

		try {
			server = new ServerSocket(port);
			while (true) {
				Socket sock = server.accept();
				IIncomeTask task = beanService.getBean(IncomeTask.class);
				task.setSocket(sock);
				executor.execute(task);
			}
		}
		catch (IOException ex) {
			PieLogger.info(this.getClass(), "Listener Error", ex);
		}

	}
}
