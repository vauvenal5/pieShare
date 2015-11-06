/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.networkService;

import java.net.BindException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.pieShare.pieTools.pieUtilities.service.networkService.api.IUdpPortService;
import org.pieShare.pieTools.pieUtilities.service.pieLogger.PieLogger;

/**
 *
 * @author RicLeo00
 */
public class UdpPortService implements IUdpPortService {
	//todo: merge with the other network service in PieShareApp
    private final int MAX_PORT;

    public UdpPortService() {
        MAX_PORT = 65535;
    }

    @Override
    public int getNewPortFrom(int port) {
        for (int i = port; i <= MAX_PORT; i++) {
            try {
                DatagramSocket s = new DatagramSocket(i);
                s.close();
                return i;
            } catch (BindException ex) {
                PieLogger.info(this.getClass(), "Port could not be found. Try next one.", ex);
            } catch (SocketException ex) {
                PieLogger.info(this.getClass(), "Port could not be found. Try next one.", ex);
            }
        }
        return -1;
    }

}
