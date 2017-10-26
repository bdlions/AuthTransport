/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet.factory;

import com.bdlions.util.REQUEST_TYPE;
import org.bdlions.transport.packet.IPacket;



/**
 *
 * @author alamgir
 */
public interface IClientPacketFactoryCallback {
    public void receiveSocketPacket(REQUEST_TYPE socketType, IPacket packet);
    public void receiveSessionAlivePacket(IPacket iPacket);
    public void receiveConfirmationPacket(IPacket iPacket);
}
