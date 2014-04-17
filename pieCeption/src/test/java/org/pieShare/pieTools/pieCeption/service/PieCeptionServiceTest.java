/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieCeption.service;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Mockito;
import org.pieShare.pieTools.pieCeption.model.action.ICommand;
import org.pieShare.pieTools.pieCeption.service.api.IConnectorService;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.api.IExecutorService;
import org.pieShare.pieTools.pieUtilities.service.pieExecutorService.exception.PieExecutorServiceException;
import org.pieShare.pieTools.pieUtilities.utils.ShouldNeverHappenError;

/**
 *
 * @author Svetoslav
 */
public class PieCeptionServiceTest {
    
    private ICommand command;
    private IConnectorService connector;
    private IExecutorService executorService;
    private IPieMessage message;
    
    private PieCeptionService instance;
    
    
    public PieCeptionServiceTest() {
    }
    
    @Before
    public void setUp() {
        command = Mockito.mock(ICommand.class);
        connector = Mockito.mock(IConnectorService.class);
        executorService = Mockito.mock(IExecutorService.class);
        message = Mockito.mock(IPieMessage.class);
        
        instance = new PieCeptionService();
        instance.setConnectorService(connector);
        instance.setExecutorService(executorService);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of handleCommand method, of class PieCeptionService.
     */
    @Test
    public void testHandleCommandIsMaster() throws Exception {
        
        Mockito.when(this.connector.isPieShareRunning()).thenReturn(false);
        Mockito.when(this.command.getMessage()).thenReturn(this.message);
        
        instance.handleCommand(command);
        
        Mockito.verify(this.executorService, Mockito.times(1)).handlePieEvent(this.message);
    }
    
    @Test
    public void testHandleCommandNotMaster() throws Exception {
        Mockito.when(this.connector.isPieShareRunning()).thenReturn(true);
        
        instance.handleCommand(this.command);
        
        Mockito.verify(this.connector, Mockito.times(1)).sendToMaster(this.command);
    }
    
    @Test(expected = ShouldNeverHappenError.class)
    public void testHandleCommandException() throws Exception {
        Mockito.when(this.connector.isPieShareRunning()).thenReturn(false);
        Mockito.when(this.command.getMessage()).thenReturn(this.message);
        
        Mockito.doThrow(new PieExecutorServiceException("Testing")).when(this.executorService).handlePieEvent(message);
        
        instance.handleCommand(this.command);
    }
}
