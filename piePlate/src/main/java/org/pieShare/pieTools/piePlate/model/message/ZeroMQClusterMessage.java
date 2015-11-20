/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.pieShare.pieTools.piePlate.model.message;

import java.net.InetAddress;
import org.pieShare.pieTools.piePlate.model.IPieAddress;
import org.pieShare.pieTools.piePlate.model.message.api.IClusterMessage;
import org.pieShare.pieTools.piePlate.model.message.api.IPiePlainTextMessage;


public class ZeroMQClusterMessage extends AClusterMessage implements IPiePlainTextMessage {
    private int port;
    private InetAddress ip;
    
    public void setPort(int port){
        this.port = port;
    }
    
    public int getPort(){
        return port;
    }
    
    public void setIpAddress(InetAddress ip){
        this.ip = ip;
    }
    
    public InetAddress getIpAddress(){
        return ip;
    }
}
