/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pieShare.pieTools.pieUtilities.service.networkService;

import java.io.IOException;
import java.net.InetAddress;

/**
 *
 * @author Svetoslav
 */
public interface INetworkService {

    void setNicDisplayName(String nicDisplayName);
            
	int getAvailablePort();

	InetAddress getLocalHost();

	InetAddress getLocalHost(boolean invalidate);
	
	int getAvailablePortStartingFrom(int port);
	
	int getNumberOfAvailablePorts(int firstPort, int lastPort);
	
	int reserveAvailablePortStartingFrom(int port) throws IOException;
	
	void freeReservedPort(int port) throws IOException;
}
