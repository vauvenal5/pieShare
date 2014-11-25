/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.cmdLineService.api;

/**
 *
 * @author Svetoslav
 */
public interface ICmdLineService {

	public void writeLine(IPrintableEvent msg);

	public void readCommand();
}
