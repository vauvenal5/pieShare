/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.tasks;

import java.util.HashMap;
import org.pieShare.pieTools.piePlate.model.UdpAddress;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.LoopHoleConnectionMessage;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.task.IPieEventTask;
import org.pieShare.pieShareServer.services.loopHoleService.api.ILoopHoleService;
import org.pieShare.pieShareServer.services.loopHoleService.api.IUserPersistanceService;
import org.pieShare.pieShareServer.services.model.User;

/**
 *
 * @author Richard
 */
public class RegisterTask implements IPieEventTask<RegisterMessage> {

	private RegisterMessage msg;
	private IUserPersistanceService userPersistanceService;
	private ILoopHoleService loopHoleService;

	public void setLoopHoleService(ILoopHoleService loopHoleService) {
		this.loopHoleService = loopHoleService;
	}

	public void setUserPersistanceService(IUserPersistanceService userPersistanceService) {
		this.userPersistanceService = userPersistanceService;
	}

	@Override
	public void setEvent(RegisterMessage msg) {
		this.msg = msg;
	}

	@Override
	public void run() {

		HashMap<String, User> sameUsers = userPersistanceService.getUsersByName(msg.getName());

		User newUser = new User();
		newUser.setId(msg.getId());
		newUser.setName(msg.getName());
		UdpAddress privateAddress = new UdpAddress();
		privateAddress.setHost(msg.getPrivateHost());
		privateAddress.setPort(msg.getPrivatePort());
		newUser.setPrivateAddress(privateAddress);
		newUser.setPublicAddress(msg.getSenderAddress());
		userPersistanceService.addUser(newUser);

		LoopHoleConnectionMessage connectionMessageToReceiver = new LoopHoleConnectionMessage();
		connectionMessageToReceiver.setClientPrivateIP(newUser.getPrivateAddress().getHost());
		connectionMessageToReceiver.setClientPrivatePort(newUser.getPrivateAddress().getPort());
		connectionMessageToReceiver.setClientPublicIP(newUser.getPublicAddress().getHost());
		connectionMessageToReceiver.setClientPublicPort(newUser.getPublicAddress().getPort());

		for (User user : sameUsers.values()) {

			if (msg.getId().equals(user.getId())) {
				continue;
			}

			LoopHoleConnectionMessage connectionMessageToSender = new LoopHoleConnectionMessage();
			connectionMessageToSender.setClientPrivateIP(user.getPrivateAddress().getHost());
			connectionMessageToSender.setClientPrivatePort(user.getPrivateAddress().getPort());
			connectionMessageToSender.setClientPublicIP(user.getPublicAddress().getHost());
			connectionMessageToSender.setClientPublicPort(user.getPublicAddress().getPort());

			loopHoleService.send(connectionMessageToSender, msg.getSenderAddress().getHost(), msg.getSenderAddress().getPort());
			loopHoleService.send(connectionMessageToReceiver, user.getPublicAddress().getHost(),user.getPublicAddress().getPort());
		}
	}
}
