/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet.analyzer;

import org.bdlions.transport.packet.IPacket;


/**
 *
 * @author saikat
 */
public interface IRelayPacketAnalyzingCallback {
    public void receiveBrokenPacket(IPacket relayPacketAttributes);
    public void receiveFullPacket(IPacket relayPacketAttributes);
    public void emptyPacket();
}
