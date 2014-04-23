/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.commandService;

import org.pieShare.pieTools.pieUtilities.model.command.ICommand;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceUser;
import org.pieShare.pieTools.pieUtilities.service.commandService.api.ICommandService;

/**
 *
 * @author Svetoslav
 */
public abstract class AbstractCommandService<T extends ICommand> extends BeanServiceUser implements ICommandService<T> {
}
