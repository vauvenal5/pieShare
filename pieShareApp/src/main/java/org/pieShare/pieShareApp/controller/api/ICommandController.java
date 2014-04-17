/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieShareApp.controller.api;

import org.pieShare.pieShareApp.service.commandService.ICommand;

/**
 *
 * @author Svetoslav
 */
public interface ICommandController<T extends ICommand> {
    void handleCommand(T command);
}
