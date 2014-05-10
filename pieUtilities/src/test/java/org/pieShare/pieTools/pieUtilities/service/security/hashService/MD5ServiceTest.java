/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.pieUtilities.service.security.hashService;

import org.bouncycastle.util.encoders.Hex;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.pieShare.pieTools.pieUtilities.service.security.BouncyCastleProviderService;
import org.pieShare.pieTools.pieUtilities.service.security.IProviderService;

/**
 *
 * @author Svetoslav
 */
public class MD5ServiceTest {

    /**
     * Test of hash method, of class MD5Service.
     */
    @Test
    public void testHash() {
        byte[] data = "testingMyService".getBytes();
        MD5Service instance = new MD5Service();
        instance.setProviderService(new BouncyCastleProviderService());
        
        String hexRes = "2e0f2cf683704a95c5c1d13e899147ad";
        byte[] expResult =  Hex.decode(hexRes);
        byte[] result = instance.hash(data);

        assertArrayEquals(expResult, result);
    }
    
}
