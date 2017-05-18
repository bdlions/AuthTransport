/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.remover;

import java.util.ArrayList;
import org.bdlions.transport.channel.AbstractChannel;
import org.bdlions.transport.channel.provider.IChannelProvider;
import org.bdlions.transport.sender.IClientPacketSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saikat
 */
public class PacketRemover extends Thread{
    private IChannelProvider channelProvider;
    private static boolean running;
    private Logger logger = LoggerFactory.getLogger(PacketRemover.class);

    public PacketRemover(IChannelProvider channelProvider){
        this.channelProvider = channelProvider;
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {       
//            logger.info("Packet Remover Thread is running...");
            try {
                ArrayList<IClientPacketSender> clientChannels = channelProvider.getClientChannelList();
                for (IClientPacketSender clientChannel : clientChannels) {
                    ((AbstractChannel)clientChannel).clearTimeoutPackets();
//                    ((AbstractChannel)clientChannel).clearTimeoutPackets(logger);
                }
                ((AbstractChannel)channelProvider.getRelayChannel()).clearTimeoutPackets();
//                ((AbstractChannel)channelProvider.getRelayChannel()).clearTimeoutPackets(logger);

                sleep(1500);
                
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage() + " in Packet Remover Thread");
            }
        }
    }
    
    public void stopService(){
        try {
            this.running = false;
            join();
            logger.info("Packet Remover Thread stopped..");
        } catch (Exception e) {
            logger.error("Exception in Packet Remover Thread.. " + e.getMessage());
        }
    }
}
