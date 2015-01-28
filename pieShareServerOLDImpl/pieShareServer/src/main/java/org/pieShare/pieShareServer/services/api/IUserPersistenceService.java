/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.api;

import javax.json.JsonObject;
import org.pieShare.pieShareServer.model.UserData;

/**
 *
 * @author Richard
 */
public interface IUserPersistenceService {

	public void addUser(String name, UserData obj);

	public UserData getUser(String name);
}
