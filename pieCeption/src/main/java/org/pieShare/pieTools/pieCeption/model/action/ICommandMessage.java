/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieCeption.model.action;

import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;

/**
 *
 * @author Svetoslav
 */
public interface ICommandMessage extends IPieMessage {
    void executeCommand();
}
