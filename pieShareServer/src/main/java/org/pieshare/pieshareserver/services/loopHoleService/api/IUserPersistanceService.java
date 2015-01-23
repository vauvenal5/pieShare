/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieshare.pieshareserver.services.loopHoleService.api;

import java.util.HashMap;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieshare.pieshareserver.services.model.User;

/**
 *
 * @author Richard
 */
public interface IUserPersistanceService {

	void addUser(User user);

	HashMap<String, User> getUsersByName(String name);

	User getUserById(String id);

	void deleteUser(String id);

}
