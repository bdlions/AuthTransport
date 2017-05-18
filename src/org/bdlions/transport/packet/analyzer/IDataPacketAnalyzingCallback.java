/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet.analyzer;

import org.bdlions.transport.packet.IPacket;

/**
 *
 * @author alamgir
 */
public interface IDataPacketAnalyzingCallback {
    public void emptyPacket();
    public void incorrectPacketFormat();
    public void incorrectSocketType();
//    public void receiveBrokenPacket(InetAddress clientIP, int clientPort, BrokenPacketAttributes brokenPckAttr);
    public void receiveFullPacket(IPacket datagramFullPacket);
}
