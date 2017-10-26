/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet;

import com.bdlions.util.ACTION;
import com.bdlions.util.REQUEST_TYPE;

/**
 *
 * @author alamgir
 */
public class PacketHeaderImpl implements IPacketHeader{

    private ACTION action ;
    private REQUEST_TYPE requestType;
    private String packetId;
    private String sessionId;
    private boolean isBroken;
    
    public void setAction(ACTION action) {
        this.action = action;
    }

    public void setRequestType(REQUEST_TYPE requestType) {
        this.requestType = requestType;
    }

    public void setPacketId(String packetId) {
        this.packetId = packetId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    
    @Override
    public ACTION getAction() {
        return action;
    }

    @Override
    public REQUEST_TYPE getRequestType() {
        return requestType;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public String getPacketId() {
        return packetId;
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }
    
}
