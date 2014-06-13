/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.cmdLineService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.pieUtilities.service.beanService.BeanServiceError;
import org.pieShare.pieTools.pieUtilities.service.beanService.IBeanService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.ICmdLineService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.IPrintableEvent;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.exception.CommandParserServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 *
 * @author Svetoslav
 */
public class CmdLineService implements ICmdLineService {

	private IExecutorService executor;
	private IBeanService beanService;
	private ICommandParserService parserService;

	private String linePrefix = "pieShare> ";

	public void setExecutorService(IExecutorService executor) {
		this.executor = executor;
	}

	public void setBeanService(IBeanService service) {
		this.beanService = service;
	}

	public void setCommandParserService(ICommandParserService service) {
		this.parserService = service;
	}

	@Override
	public void writeLine(IPrintableEvent msg) {
		System.out.println(msg.getText());
		System.out.print(this.linePrefix);

		try {
			this.executor.execute(beanService.getBean(ReadLineTask.class));
		} catch (BeanServiceError ex) {
			//todo-sv: error handling
			//should never happen!!!
		}
	}

	@Override
	public void readCommand() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		try {
			String cmd = reader.readLine();
			String[] args = cmd.split(" ");
			this.parserService.parseArgs(args);
		} catch (IOException ex) {
			//todo-sv: error handling
		} catch (CommandParserServiceException ex) {
			//todo-sv: error handling
		}
	}

}
