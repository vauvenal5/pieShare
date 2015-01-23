/*


 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services;

import java.util.HashMap;
import org.pieShare.pieShareServer.model.UserData;
import org.pieShare.pieShareServer.services.api.IUserPersistenceService;

/**
 *
 * @author Richard
 */
public class UserPersistenceService implements IUserPersistenceService {

	private final HashMap<String, UserData> list;

	public UserPersistenceService() {
		this.list = new HashMap<String, UserData>();
	}

	public void addUser(String name, UserData obj) {
		list.put(name, obj);
	}

	public UserData getUser(String name) {
		return list.get(name);
	}

	public void removeUser(String name) {
		list.remove(name);
	}

}
