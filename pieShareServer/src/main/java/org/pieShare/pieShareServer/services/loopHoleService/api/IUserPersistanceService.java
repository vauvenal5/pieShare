/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieShareServer.services.loopHoleService.api;

import java.util.HashMap;
import org.pieShare.pieTools.piePlate.model.message.loopHoleMessages.RegisterMessage;
import org.pieShare.pieShareServer.services.model.User;

/**
 *
 * @author Richard
 */
public interface IUserPersistanceService {

    void addUser(User user);

    HashMap<String, User> getConnectedUsersByName(String name);

    HashMap<String, User> getNonConnectedUsersByName(String name);

    User getUserById(String id);

    void deleteUser(String id);

}
