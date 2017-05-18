/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet;

import com.auction.util.ACTION;
import com.auction.util.REQUEST_TYPE;
import java.net.InetAddress;
import org.bdlions.transport.sender.IClientPacketSender;
import org.bdlions.transport.sender.IRelayPacketSender;

/**
 *
 * @author alamgir
 */
public class PacketImpl implements IPacket {

    private IPacketHeader packetHeader;
    private String packetBody;
    private byte[] data;
    private InetAddress clientAddress;
    private int clientPort;
    private IClientPacketSender clientPacketSender;
    private byte[] packetHeaderData;

    
    public void setPacketHeader(IPacketHeader packetHeader){
        this.packetHeader = packetHeader;
    }
    
    @Override
    public IPacketHeader getPacketHeader() {
        return packetHeader;
    }

    public void setPacketBody(String packetBody){
        this.packetBody = packetBody;
    }
    
    @Override
    public String getPacketBody() {
        return packetBody;
    }

    @Override
    public InetAddress getRemoteIP() {
        return clientAddress;
    }

    public void setRemoteIP(InetAddress clientAddress){
        this.clientAddress = clientAddress;
    }
    
    @Override
    public int getRemotePort() {
        return clientPort;
    }
    
    public void setRemotePort(int clientPort){
        this.clientPort = clientPort;
    }

    @Override
    public long getSentTime() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSentCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int setSentCount(int count) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IClientPacketSender getClientPacketSender() {
        return clientPacketSender;
    }
    
    public void setClientPacketSender(IClientPacketSender clientPacketSender){
        this.clientPacketSender = clientPacketSender;
    }

    @Override
    public IRelayPacketSender getRelayPacketSender() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void setResponseData(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] getResponseData() {
        return data;
    }

    @Override
    public byte[] getPacketHeaderData() {
        return packetHeaderData;
    }

    public void setPacketHeaderData(byte[] data){
        this.packetHeaderData = data;
    }

}

