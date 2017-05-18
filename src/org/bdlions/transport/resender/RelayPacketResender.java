/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.resender;

import java.util.concurrent.TimeUnit;
import org.bdlions.transport.channel.AbstractChannel;
import org.bdlions.transport.sender.IRelayPacketSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saikat
 */
public class RelayPacketResender extends Thread {

    private final IRelayPacketSender relayPacketSender;
    private boolean running;
    private final Logger logger ;
    private final int TW0_SECONDS = 2 * 1000 * 60;
    
    public RelayPacketResender(IRelayPacketSender packetSender) {
        this.relayPacketSender = packetSender;
        this.running = true;
        this.logger = LoggerFactory.getLogger(RelayPacketResender.class);
    }

    @Override
    public void run() {
        while (running) {
            try {
//                logger.info("Relay Packet Resender is running..");
                AbstractChannel relayChannel = (AbstractChannel) relayPacketSender;
                relayChannel.resendPackets();
                TimeUnit.MILLISECONDS.sleep(TW0_SECONDS);
            } catch (Exception e) {
                logger.error("Exception in RelayPacker Resender. " + e.getMessage(),e);
            }
        }
    }
    
    public void stopService(){
        try {
            this.running = false;
            join();
            logger.info("Relay Packet Resender is stopped .. ");
        } catch (InterruptedException ex) {
            logger.error("Exception in stopping Relay Packet Resender.." + ex.getMessage(),ex);
        }
    }

}
