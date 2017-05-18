/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author alamgir
 * ClientPacketFactory class for producing packet
 * and return packet to the originator
 * this will use call back to return packet
 */

public class ClientPacketFactoryWorker {
    private final ExecutorService service;
    private final int POOL_SIZE = 100;

    public ClientPacketFactoryWorker() {
        service = Executors.newFixedThreadPool(POOL_SIZE);
    }

    public void doWork(ClientPacketFactory packetFactory){
        service.execute(packetFactory);
    }
    
    public void shutdown(){
        service.shutdown();
    }
}
