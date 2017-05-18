/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.processor.packet;

import java.net.DatagramPacket;

/**
 *
 * @author alamgir
 */
public interface IPacketProcessor {
    public void process(DatagramPacket packet);
}
