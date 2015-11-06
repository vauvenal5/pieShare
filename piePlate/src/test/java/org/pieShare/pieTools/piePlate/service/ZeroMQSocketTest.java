/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.piePlate.service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import static org.junit.Assert.assertArrayEquals;
import org.junit.Test;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieDealer;
import org.pieShare.pieTools.piePlate.service.cluster.zeroMqCluster.socket.PieRouter;

public class ZeroMQSocketTest {

    //@Test
    public void testConnectionDealerToRouter() {
    }

    @Test
    public void testSendRecv() {
        Thread n = new Thread(new Runnable() {
            @Override
            public void run() {
                PieDealer dealer = new PieDealer();
                try {
                    InetAddress routerAddress = InetAddress.getLocalHost();
                    
                    dealer.connect(routerAddress, 9000);

                } catch (UnknownHostException e) {

                }

                byte[] messageSend = new byte[]{1,2,3,4,5,6,7,8,9,10};
                dealer.send(messageSend);

                dealer.close();
            }
        });
        
        byte[] messageSend = new byte[]{1,2,3,4,5,6,7,8,9,10};
        byte[] messageRecv = null;
        
        PieRouter router = new PieRouter();
        try {
            InetAddress routerAddress = InetAddress.getLocalHost();
            router.bind(routerAddress, 9000);
            
            n.start();
            
            messageRecv = router.receive();
            
            router.close();
            
        } catch (UnknownHostException e) {
            System.out.println(e.toString());
        }
        
        assertArrayEquals(messageSend, messageRecv);
    }
}
