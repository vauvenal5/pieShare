/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.cmdLineService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.ICmdLineService;
import org.pieShare.pieTools.pieUtilities.service.cmdLineService.api.IPrintableMessage;
import org.pieShare.pieTools.pieUtilities.service.commandParser.api.ICommandParserService;
import org.pieShare.pieTools.pieUtilities.service.commandParser.exception.CommandParserServiceException;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;

/**
 *
 * @author Svetoslav
 */
public class CmdLineService implements ICmdLineService {
    
    private IExecutorService executor;
    
    private ICommandParserService parserService;
    
    private String linePrefix = "pieShare> ";

    @Override
    public void writeLine(IPrintableMessage msg) {
        System.out.println(msg.getText());
        System.out.print(this.linePrefix);
        this.executor.execute(new ReadLineTask());
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
