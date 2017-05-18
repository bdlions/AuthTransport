/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.processor.packet;

import java.net.DatagramPacket;
import org.bdlions.transport.channel.IChannel;
import org.bdlions.transport.channel.relay.RelayChannelImpl;
import org.bdlions.transport.packet.IPacket;
import org.bdlions.transport.packet.analyzer.IRelayPacketAnalyzingCallback;
import org.bdlions.transport.packet.analyzer.RelayPacketAnalyzer;
import org.bdlions.transport.sender.IRelayPacketSender;
import org.bdlions.util.handler.request.IClientRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author saikat
 */
public class RelayPacketProcessor implements IPacketProcessor {

    private final IRelayPacketSender relayChannel;
    private final IClientRequestHandler handler;
//    private final ReceiveBrokenPacketFromRelay brokenPacketFromRelay;
    private final Logger logger;
    private final RelayPacketAnalyzer relayPacketAnalyzer;
    private DatagramPacket received_packet;

    public RelayPacketProcessor(IChannel channelImpl, IClientRequestHandler handler) {
        this.relayChannel = (IRelayPacketSender) channelImpl;
        this.handler = handler;
//        brokenPacketFromRelay = new ReceiveBrokenPacketFromRelay(relayChannel);
        logger = LoggerFactory.getLogger(this.getClass());
        this.relayPacketAnalyzer = new RelayPacketAnalyzer();
    }

    @Override
    public synchronized void process(DatagramPacket packet) {
        this.received_packet = packet;

        logger.trace("Relay packet received from {\"ip\": " + packet.getAddress().getHostAddress() + ", \"port\": " + packet.getPort() + "} is processing");
//        relayPacketAnalyzer.analyzePacket(packet, new IRelayPacketAnalyzingCallback() {
//            @Override
//            public void receiveBrokenPacket(IPacket packet) {
////                if (packet.getAction() != AppConstants.ACTION_CONFIRMATION && packet.getServerPacketID() > 0) {
////                    sendConfirmation(packet.getServerPacketId());
////                    logger.trace("confirmation has been sent to relay {\"ip\" : " + received_packet.getAddress() + " , \"port\" : " + received_packet.getPort()
////                            + " ,\"serverPacketId\" : " + packet.getServerPacketID() + "}");
////                }
//            }
//
//            @Override
//            public void receiveFullPacket(IPacket packet) {
//                logger.trace("received request from " + received_packet.getAddress().getHostAddress() + ":" + received_packet.getPort() + " to socket : " + packet.getSocketType() + " for relay action : " + packet.getAction());
//                if (packet.getAction() != AppConstants.ACTION_CONFIRMATION && packet.getServerPacketID() > 0) {
//                    sendConfirmation(packet.getServerPacketID());
//                    logger.trace("confirmation has been sent to relay {\"ip\" : " + received_packet.getAddress() + " , \"port\" : " + received_packet.getPort()
//                            + " ,\"serverPacketId\" : " + packet.getServerPacketID() + "}");
//                } else if (packet.getAction() == AppConstants.ACTION_CONFIRMATION && packet.getServerPacketID() > 0) {
//                    RelayChannelImpl relayChannelImpl = (RelayChannelImpl) relayChannel;
//                    relayChannelImpl.removeServerPacket(packet.getServerPacketID());
//                    return;
//                }
//
//                RelayPacketAttributes relayPacket = (RelayPacketAttributes) packet;
//                if (relayChannel.requestAccepted(relayPacket.getUserID(), relayPacket.getServerPacketID())) {
//                    relayPacket.setClientAddress(received_packet.getAddress());
//                    relayPacket.setClientPort(received_packet.getPort());
//                    relayPacket.setRelayPacketSender(relayChannel);
//                    handler.receiveRequest(relayPacket);
//                } else {
//                    logger.error("relay request is not excepted for {\"userId\" : " + relayPacket.getUserID() + ", \"serverPacketId\" : " + relayPacket.getServerPacketID() + "}");
//                }
//            }
//
//            @Override
//            public void emptyPacket() {
//                logger.info("Empty packet received from {\"ip\": " + received_packet.getAddress().getHostAddress() + ", \"port\": " + received_packet.getPort()+"}");
//            }
//
//        });
        logger.trace("Relay packet received from {\"ip\": " + received_packet.getAddress().getHostAddress() + ", \"port\": " + received_packet.getPort()+"}");
    }

//    private void sendConfirmation(long serverPacketID) {
//        PacketBuilder builder = new PacketBuilder(true, false);
//        builder.addInt(AttributeCodes.ACTION, AppConstants.ACTION_CONFIRMATION, 2)
//                .addLong(AttributeCodes.SERVER_PACKET_ID, serverPacketID, 8);
//        GenericPacket packetImpl = new GenericPacket();
//        packetImpl.setData(builder.getByteContents());
//        packetImpl.setClientAddress(received_packet.getAddress());
//        packetImpl.setClientPort(received_packet.getPort());
//        packetImpl.setStore(false);
//        relayChannel.send(packetImpl);
//    }
}
