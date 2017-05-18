/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.resender;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import org.bdlions.transport.channel.AbstractChannel;
import org.bdlions.transport.sender.IClientPacketSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saikat
 */
public class ServerPacketResender extends Thread {

    private final ArrayList<IClientPacketSender> clientPacketSenders;
    private boolean running;
    private final Logger logger;
    private final int RESEND_TIMEOUT = 10;

    public ServerPacketResender(ArrayList<IClientPacketSender> channelList) {
        this.clientPacketSenders = channelList;
        this.running = true;
        this.logger = LoggerFactory.getLogger(ServerPacketResender.class);
    }

    @Override
    public void run() {
        while (running) {
            try {
//                logger.info("Server Packet Resender Thread is running");
                for (IClientPacketSender clientPacketSender : clientPacketSenders) {
                    AbstractChannel channel = (AbstractChannel) clientPacketSender;
                    channel.resendPackets();
                }
                TimeUnit.MILLISECONDS.sleep(RESEND_TIMEOUT);
//                logger.info("Server Packet Resender is sleeping");
            } catch (Exception e) {
                logger.error("exception in server packet resender",e);
            }
        }
    }

    public void stopService() {
        try {
            this.running = false;
            join(1L);
            logger.info("Server Packet Resender has stopped");
        } catch (InterruptedException ex) {
            logger.error("Exeption in stopping Server Packet Resender",ex);
        }
    }

}
