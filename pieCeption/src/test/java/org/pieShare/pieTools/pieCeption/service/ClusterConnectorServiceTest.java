/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieCeption.service;

import java.net.InetAddress;
import junit.framework.Assert;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.pieShare.pieTools.pieCeption.model.action.ICommand;
import org.pieShare.pieTools.piePlate.model.message.api.IPieMessage;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterManagementService;
import org.pieShare.pieTools.piePlate.service.cluster.api.IClusterService;

/**
 *
 * @author Svetoslav
 */
public class ClusterConnectorServiceTest {
    
    private IClusterManagementService clusterManagement;
    private IClusterService cluster;
    
    private ClusterConnectorService instance;
    
    public ClusterConnectorServiceTest() {
    }
    
    @Before
    public void setUp() throws Exception {
        this.clusterManagement = Mockito.mock(IClusterManagementService.class);
        this.cluster = Mockito.mock(IClusterService.class);
        
        this.instance = new ClusterConnectorService();
        
        this.instance.setClusterManagementService(clusterManagement);
        
        String name = InetAddress.getLocalHost().getHostName();
        
        Mockito.when(this.clusterManagement.connect(name)).thenReturn(cluster);
        
        this.instance.init();
    }

    /**
     * Test of isPieShareRunning method, of class ClusterConnectorService.
     */
    @Test
    public void testIsPieShareRunningTrue() {
        Mockito.when(this.cluster.getMembersCount()).thenReturn(2);
        Assert.assertEquals(true, this.instance.isPieShareRunning());
    }
    
    /**
     * Test of isPieShareRunning method, of class ClusterConnectorService.
     */
    @Test
    public void testIsPieShareRunningFalse() {
        Mockito.when(this.cluster.getMembersCount()).thenReturn(1);
        Assert.assertEquals(false, this.instance.isPieShareRunning());
    }    
}
