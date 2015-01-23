/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.loopHoleService;

import java.util.HashMap;
import java.util.function.BiConsumer;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieShareServer.services.loopHoleService.api.IUserPersistanceService;
import org.pieShare.pieShareServer.services.model.User;

/**
 *
 * @author Richard
 */
public class UserPersistanceService implements IUserPersistanceService {

	private HashMap<String, User> newUsers;

	public UserPersistanceService() {
		this.newUsers = new HashMap<>();
	}

	@Override
	public synchronized void addUser(User msg) {
		newUsers.put(msg.getId(), msg);
	}

	@Override
	public synchronized HashMap<String, User> getUsersByName(String name) {
		HashMap<String, User> returnUsers = new HashMap<>();
		newUsers.forEach((k, v) -> {
			if (v.getName().equals(name)) {
				returnUsers.put(k, v);
			}
		});
		return returnUsers;
	}

	@Override
	public synchronized User getUserById(String id) {
		return newUsers.get(id);
	}

	@Override
	public synchronized void deleteUser(String id) {
		newUsers.remove(id);
	}

}
