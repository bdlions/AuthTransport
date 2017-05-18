/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.channel.relay;

import com.google.gson.Gson;
import org.bdlions.transport.sender.IRelayPacketSender;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
//import org.bdlions.transport.packet.GenericPacket;
import org.bdlions.session.ISession;
import org.bdlions.session.ISessionManager;
import org.bdlions.transport.channel.AbstractChannel;
import org.bdlions.transport.packet.IPacket;
import org.bdlions.transport.packet.IRepositoryCallBack;
import org.bdlions.transport.processor.packet.IPacketProcessor;
import org.bdlions.transport.processor.packet.RelayPacketProcessor;
import org.bdlions.transport.repository.SimpleRepository;
import org.bdlions.util.handler.request.IClientRequestHandler;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class RelayChannelImpl extends AbstractChannel implements IRelayPacketSender {
    
    private final int RELAY_CHANNEL_PACKET_CAPACITY = 50000;
    private final LinkedBlockingQueue pktProcessingQueue;
    private final IPacketProcessor pktProcessor;
    
    private final InetAddress relayServerIp;
    private final int relayServerPort;
    
    private final ISessionManager sessionManager;
    private final org.slf4j.Logger logger;
    private final SimpleRepository<Long, IPacket> relayPacketRepository;
    private final SimpleRepository<String, Long> receivedRequestRepo;
    
    public RelayChannelImpl(int port, IClientRequestHandler handler, ISessionManager sessionManager) throws UnknownHostException {
        super("Relay Channel 1", port, sessionManager);
        this.sessionManager = sessionManager;
        relayServerIp = InetAddress.getLocalHost();
        relayServerPort = 10000;
        
        pktProcessingQueue = new LinkedBlockingQueue<>(RELAY_CHANNEL_PACKET_CAPACITY);
        pktProcessor = new RelayPacketProcessor(this, handler);
        
        logger = LoggerFactory.getLogger(RelayChannelImpl.class);
        this.relayPacketRepository = new SimpleRepository<>(1000, 10000);
        this.receivedRequestRepo = new SimpleRepository<>(1000 / 100, 30000);
    }

    @Deprecated
    @Override
    public void send(byte[] data) {
//        if (data != null && data.length > 0) {
//            send(data, relayServerIp, relayServerPort);
//        } else {
//            logger.error("corrupted data");
//        }
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public void send(ISession session, IPacket packet) {
//        byte[] data = packet.getData();
//        if (session != null && data != null && data.length > 0) {
//            InetAddress clientIP = session.getRemoteIP();
//            int clientPort = session.getRemotePort();
//            send(data, clientIP, clientPort);
//        } else {
//            logger.error("corrupted data");
//        }
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void send(IPacket packet) {
//        if (packet != null) {
//            byte[] data = packet.getData();
//            if (data != null && data.length > 0) {
//                InetAddress clientIP = packet.getClientAddress();
//                int clientPort = packet.getClientPort();
//                send(data, clientIP, clientPort);
//            } else {
//                logger.error("corrupted relay data for { \"action\" : " + packet.getAction() + "}");
//            }
//            if (packet.isStorable()) {
//                storeRelayPacket(packet);
//            }
//        } else {
//            logger.error("relay packet is null");
//        }
        
    }
    
    @Override
//    public void clearTimeoutPackets(Logger l) {
    public void clearTimeoutPackets() {
//        l.debug(getId() + " repository ********************");
//        l.debug("BEFORE");
//        l.debug("relay packet repository size : " + relayPacketRepository.getSize());
//        l.debug("relay received request repository size : " + receivedRequestRepo.getSize());
        relayPacketRepository.clearTimeoutPackets();
        receivedRequestRepo.clearTimeoutPackets();
//        l.debug("AFTER");
//        l.debug("relay packet repository size : " + relayPacketRepository.getSize());
//        l.debug("relay received request repository size : " + receivedRequestRepo.getSize());
//        l.debug("******************************************************************");
    }
    
    @Override
    public void resendPackets() {
        relayPacketRepository.resendPackets(new IRepositoryCallBack() {
            @Override
            public void setPacket(IPacket packet) {
                send(packet);
            }
        });
    }
    
    @Override
    public LinkedBlockingQueue getPacketProcessingQueue() {
        return pktProcessingQueue;
    }
    
    @Override
    public IPacketProcessor getPacketProcessor() {
        return pktProcessor;
    }
    
    private void storeRelayPacket(IPacket packet) {
//        GenericPacket genericPacket = (GenericPacket) packet;
//        genericPacket.setSentTime(System.currentTimeMillis());
//        long serverPacketId = genericPacket.getServerPacketId();
//        if (serverPacketId > 0) {
//            if (!relayPacketRepository.containsKey(serverPacketId)) {
//                try {
//                    relayPacketRepository.put(serverPacketId, genericPacket);
//                    logger.trace("Store relay packet against for { \"serverPacketId\" : " + serverPacketId + " , \"action\" : " + packet.getAction() + "}");
//                } catch (Exception ex) {
//                    logger.error(ex.getMessage() + ".. Error in storing relay packet for { \"serverPacketId\" : " + serverPacketId + " , \"action\" : " + packet.getAction() + "}", ex);
//                }
//            } else {
//                logger.error("already relay packet exists for { \"serverPacketId\" : " + serverPacketId + " , \"action\" : " + packet.getAction() + "}");
//            }
//        } else {
//            logger.error("Invalid serverPacketId .. { \"action\": " + packet.getAction() + " , \"serverPacketId: \"" + serverPacketId + ", \"iPacket\": "+new Gson().toJson(packet)+"}");
//        }
        
    }
    
    @Override
    public boolean requestAccepted(long userId, long serverPacketId) {
        if (userId > 0 && serverPacketId > 0) {
            String key = userId + "-" + serverPacketId;
            if (!receivedRequestRepo.containsKey(key)) {
                try {
                    receivedRequestRepo.put(key, System.currentTimeMillis());
                    return true;
                } catch (Exception ex) {
                    logger.error(ex.getMessage() + " for " + key, ex);
                }
            }
        } else {
            logger.error("userId or serverPacketId is invalid in relay . {\"userId\": " + userId + " , \"serverPacketId\": " + serverPacketId + "}");
        }
        
        return false;
    }
    
    public void removeServerPacket(long serverPacketID) {
        relayPacketRepository.remove(serverPacketID);
        logger.trace("removed relay packet for { \"serverPacketId\" : " + serverPacketID + "}");
    }

//    public static void main(String[] args) throws UnknownHostException {
//        GenericPacket genericPacket = new GenericPacket();
//        genericPacket.setData("Hello".getBytes());
//        RelayChannelImpl relayChannelImpl = new RelayChannelImpl(50, null, null);
//        relayChannelImpl.storeRelayPacket(genericPacket);
//    }
}
