/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.channel.client;

import com.google.gson.GsonBuilder;
import java.net.InetAddress;
import org.bdlions.transport.sender.IClientPacketSender;
import java.util.concurrent.LinkedBlockingQueue;
import org.bdlions.session.ISession;
import org.bdlions.session.ISessionManager;
import org.bdlions.transport.channel.AbstractChannel;
import org.bdlions.transport.packet.IPacket;
import org.bdlions.transport.packet.IRepositoryCallBack;
import org.bdlions.transport.processor.packet.ClientPacketProcessor;
import org.bdlions.transport.processor.packet.IPacketProcessor;
import org.bdlions.transport.repository.HierarchicalRepository;
import org.bdlions.transport.repository.SimpleRepository;
import org.bdlions.util.handler.request.IClientRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class ClientChannelImpl extends AbstractChannel implements IClientPacketSender {

    private final int CLIENT_CHANNEL_PACKET_CAPACITY = 50000;
    private final LinkedBlockingQueue pktProcessingQueue;
    private final IPacketProcessor pktProcessor;
    private final long REPLIED_PKT_TIME_OUT = 15000;
    private final int REPLIED_PKT_REPO_SIZE = 50000;
    private final SimpleRepository<String, IPacket> repliedPacketRepo;
    private final SimpleRepository<String, Long> requestTimeMap;
    private final HierarchicalRepository<Integer, Long, IPacket> resenderPackets;
    private final HierarchicalRepository<String, Integer, IPacket> missingFullPackets;
    private final HierarchicalRepository<Long, Integer, IPacket> missingBrokenPackets;
    private final Logger logger;

    public ClientChannelImpl(String channelId, int port, IClientRequestHandler handler, ISessionManager sessionManager) {
        super(channelId, port, sessionManager);
        pktProcessingQueue = new LinkedBlockingQueue<>(CLIENT_CHANNEL_PACKET_CAPACITY);
        repliedPacketRepo = new SimpleRepository(REPLIED_PKT_REPO_SIZE, REPLIED_PKT_TIME_OUT);
        pktProcessor = new ClientPacketProcessor(this, handler, sessionManager);
        resenderPackets = new HierarchicalRepository<>(16, 3000);
        missingFullPackets = new HierarchicalRepository<>(1000 / 100, 3000);
        this.requestTimeMap = new SimpleRepository<>(1000 / 100, 5000);
        logger = LoggerFactory.getLogger(getClass());
       missingBrokenPackets = new HierarchicalRepository<>(1000, 1000);
               
    }

    public HierarchicalRepository<Integer, Long, IPacket> getResenderPackets() {
        return resenderPackets;
    }

    @Override
    public HierarchicalRepository getMissingFullPackRepository() {
        return missingFullPackets;
    }

    @Override
    public HierarchicalRepository getMissingBrokenPackRepository() {
        return missingBrokenPackets;
    }

    /**
     *
     * @param clientSession
     * @param data
     * @deprecated
     */
    @Deprecated
    @Override
    public void send(ISession clientSession, byte[] data) {
//        if (clientSession != null && data != null && data.length > 0) {
//            InetAddress clientIP = clientSession.getRemoteIP();
//            int clientPort = clientSession.getRemotePort();
//            send(data, clientIP, clientPort);
//        } else {
//            logger.error("Invalid session/corrupted data");
//        }
        throw new UnsupportedOperationException();
    }

    @Override
    public SimpleRepository getRepliedPacketRepository() {
        return repliedPacketRepo;
    }

    @Override
    public LinkedBlockingQueue getPacketProcessingQueue() {
        return pktProcessingQueue;
    }

    @Override
    public IPacketProcessor getPacketProcessor() {
        return pktProcessor;
    }

    /**
     * *
     * clear all packets for repository when time limit exceeded
     *
     * @param l
     */
    @Override
//    public void clearTimeoutPackets(Logger l) {
    public void clearTimeoutPackets() {
        //clear pktProcessor repos
        //clear repliedPacketRepo
//        l.debug(getId() + " repositories ********************************** ");
//        l.debug("BEFORE");
//        l.debug("replied packet repo size : " + repliedPacketRepo.getSize());
//        l.debug("received request repo size : " + requestTimeMap.getSize());
//        l.debug("resender packet repo size : " + resenderPackets.getSize());
//        l.debug("missing full packet repo size : " + missingFullPackets.getSize());
//        l.debug("missing broken packet repo size : " + missingBrokenPackets.getSize());

        repliedPacketRepo.clearTimeoutPackets();
        resenderPackets.clearTimeoutPackets();
        requestTimeMap.clearTimeoutPackets();
        missingBrokenPackets.clearTimeoutPackets();
        missingFullPackets.clearTimeoutPackets();

//        l.debug("AFTER");
//        l.debug("replied packet repo size : " + repliedPacketRepo.getSize());
//        l.debug("received request repo size : " + requestTimeMap.getSize());
//        l.debug("resender packet repo size : " + resenderPackets.getSize());
//        l.debug("missing full packet repo size : " + missingFullPackets.getSize());
//        l.debug("missing broken packet repo size : " + missingBrokenPackets.getSize());
//        l.debug("*****************************************************************");
    }

    /**
     * *
     * resend all packets
     */
    @Override
    public void resendPackets() {
        //resend replied packets
        resenderPackets.resendPackets(new IRepositoryCallBack() {
            @Override
            public void setPacket(IPacket packet) {
                reSend(packet);
            }
        });
//        logger.debug(this.getId() + " resend packets");
    }

    /**
     * *
     * is request in already processed state
     *
     * @param userName
     * @param action
     * @return
     */
    @Override
    public boolean isRequestProcessing(String userName, int action) {
        String key = userName + "_" + action;
        if (key != null) {
            if (requestTimeMap.containsKey(key)) {
                long lastReqTime = getRequestTime(key);
                if (System.currentTimeMillis() - lastReqTime > 10L) {
                    try {
                        requestTimeMap.put(key, System.currentTimeMillis());
                        logger.trace("key : " + key + " doesn't exist.. pass request to handler layer");
                        return true;
                    }
                    catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                    }
                }
            }
            else {
                try {
                    requestTimeMap.put(key, System.currentTimeMillis());
                    logger.trace("key : " + key + " doesn't exist.. pass request to handler layer");
                    return true;
                }
                catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
        logger.trace("key : " + key + " already exists.....");
        return false;
    }

    private long getRequestTime(String key) {
        return requestTimeMap.get(key);
    }

    @Override
    public void send(ISession clientSession, IPacket packet) {
        try{
            int packetHeaderLengthSize = 2;
            int packetBodyLengthSize = 2;
            
            byte[] data = packet.getResponseData();
            InetAddress clientIP = packet.getRemoteIP();
            int clientPort = packet.getRemotePort();
         
//            byte[] sendPacket = new byte[2 + packet.getPacketHeaderData().length];
//            
//            sendPacket[0] = (byte) ((packet.getPacketHeaderData().length >> 8) & 0xFF);
//            sendPacket[1] = (byte) (packet.getPacketHeaderData().length & 0xFF);
//            
//            System.arraycopy(packet.getPacketHeaderData(), 0, sendPacket, 2, packet.getPacketHeaderData().length);
            
            
            int packetSize = packetHeaderLengthSize + packetBodyLengthSize + packet.getPacketHeaderData().length  + packet.getResponseData().length;
            byte[] sendPacket = new byte[ packetSize ];
            
            int start = 0;
            sendPacket[start ++ ] = (byte) ((packet.getPacketHeaderData().length  >> 8) & 0xFF);
            sendPacket[start ++ ] = (byte) (packet.getPacketHeaderData().length  & 0xFF);
            
            
            System.arraycopy(packet.getPacketHeaderData(), 0, sendPacket, start, packet.getPacketHeaderData().length );
            start += packet.getPacketHeaderData().length ;
            
            sendPacket[start ++] = (byte) ((packet.getResponseData().length >> 8) & 0xFF);
            sendPacket[start ++] = (byte) (packet.getResponseData().length & 0xFF);
            
            System.arraycopy(packet.getResponseData(), 0, sendPacket, start, packet.getResponseData().length);
            /**
             * send to the client
             */
            send(sendPacket, clientIP, clientPort);
            //logger.trace("sent packet to userID : " + clientSession.getUserId());
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
//        try {
//            byte[] data = packet.getData();
//            if (clientSession != null && data != null && data.length > 0) {
//                /**
//                 * *
//                 * store as cache before sending so that we will not send
//                 * duplicate packets multiple times
//                 */
//                if (packet.isSuccess()) {
//                    if (packet instanceof ServerPacket) {
//                        cacheServerPacket((ServerPacket) packet);
//                    }
//                    else {
//                        cacheRepliedPacket(packet);
//                    }
//                }
//
//                /**
//                 * *
//                 * get client ip and port from session
//                 */
//                InetAddress clientIP = clientSession.getRemoteIP();
//                int clientPort = clientSession.getRemotePort();
//
//                /**
//                 * send to the client
//                 */
//                send(data, clientIP, clientPort);
//                logger.trace("sent packet to userID : " + clientSession.getUserId());
//            }
//            else {
//                logger.error("Invalid session/corrupted data");
//            }
//
//        }
//        catch (Exception ex) {
//            logger.error("error in sending packet in " + getId() + "-->" + ex.getMessage(), ex);
//        }
    }

    /**
     * resend packet if needed
     *
     * @param packet
     */
    @Override
    public void reSend(IPacket packet) {
//        if (packet != null) {
//            String sessionId = packet.getSessionId();
//            if (sessionId != null && !sessionId.isEmpty()) {
//                ISession clientSession = getSessionManager().getSessionBySessionId(sessionId);
//                if (clientSession != null) {
//                    InetAddress clientIP = clientSession.getRemoteIP();
//                    int clientPort = clientSession.getRemotePort();
//                    send(packet.getData(), clientIP, clientPort);
//                }
//                else {
//                    logger.error("client session is null for \"action\" : " + packet.getAction() + " , \"serverPacketId\" : " + packet.getServerPacketID() + "}");
//                }
//            } else {
//                logger.error("session id is null for { \"action\" : " + packet.getAction() + " , \"serverPacketId\" : " + packet.getServerPacketID() +"}");
//            }
//        }
//        else {
//            logger.error("resend packet is null");
//        }
    }

    /**
     * cache replied packets
     *
     * @param packet
     */
    private void cacheRepliedPacket(IPacket packet) {
//        try {
//            String clientPacketId = packet.getClientPacketID();
//            if (clientPacketId != null && !clientPacketId.isEmpty() && !repliedPacketRepo.containsKey(clientPacketId)) {
//                repliedPacketRepo.put(clientPacketId, packet);
//            }
//        }
//        catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
    }

    /**
     * cache serverpacket
     *
     * @param serverPacket
     */
//    private void cacheServerPacket(ServerPacket serverPacket) {
//        serverPacket.setSentTime(System.currentTimeMillis());
////            serverPacket.setUserID(session.getUniqueId());
//        int action = serverPacket.getAction();
//        long serverPacketId = serverPacket.getServerPacketID();
//        if (action > 0 && serverPacketId > 0) {
//            try {
//                if (resenderPackets.get(action, serverPacketId) == null) {
//                    resenderPackets.put(action, serverPacketId, serverPacket);
//
//                    int sequence = serverPacket.getSequence();
//                    if (sequence > 0) {
//                        if (serverPacket.isBroken()) {
//                            long uniqueKey = serverPacket.getUniqueKey();
//                            if (uniqueKey > 0) {
//                                if (missingBrokenPackets.get(uniqueKey, sequence) == null) {
//                                    missingBrokenPackets.put(uniqueKey, sequence, serverPacket);
//                                    logger.trace("Put in missing broken packet repository { \"action\": " + action + " , \"uniquekey\": " + uniqueKey + " , \"seq\" : " + sequence + "}");
//                                }
//                                else {
//                                    logger.error("Broken packet already exists for { \"action\": " + action + " , \"uniquekey\": " + uniqueKey + " , \"seq\" : " + sequence + "}");
//                                }
//                            }
//                            else {
//                                logger.error("Unique key is not positive for { \"action\" :" + action + " , \"serverPacketId\" : " + serverPacketId);
//                            }
//
//                        }
//                        else {
//                            String clientPacketId = serverPacket.getClientPacketID();
//                            if (clientPacketId != null && !clientPacketId.isEmpty()) {
//                                if (missingFullPackets.get(clientPacketId, sequence) == null) {
//                                    missingFullPackets.put(clientPacketId, sequence, serverPacket);
//                                    logger.trace("put in missing full packet repository { \"action\": " + action + " , \"clientPckId\": " + clientPacketId + " , \"seq\" : " + sequence + "}");
//                                }
//                                else {
//                                    logger.error("Full packet already exists for { \"action\": " + action + " , \"clientPckId\": " + clientPacketId + " , \"seq\" : " + sequence + "}");
//                                }
//                            }
//                            else {
//                                logger.error("ClientPckId is null for { \"action\" :" + action + " , \"serverPacketId\" : " + serverPacketId + "}");
//                            }
//                        }
//                    }
//                    else {
//                        logger.trace("ServerPacket's sequence is zero for { \"serverPacketId\" : " + serverPacketId + ", \"action\" : " + action);
//                    }
//                }
//            } catch (DuplicacyException e) {
//                logger.error(e.getMessage() + " for { \"unique key\" : " + serverPacket.getUniqueKey() + " , \"sequence\" : " + serverPacket.getSequence() + " , \"action\" : " + action + " , \"serverPacketId\" : " + serverPacketId + "}", e);
//            } catch (Exception ex) {
//                logger.error(ex.getMessage() + ".... error in storing serverPacket for { \"action\" : " + serverPacket.getAction() + " , \"serverPacketId\" : " + serverPacket.getServerPacketID() + "}");
//            }
//        }
//        else {
//            logger.error("invalid action or server packet id. { \"action\" : " + action + " , \"serverPacketId\" : " + serverPacketId + "}");
//        }
//
//    }

    public void printPacketSendRcvStat(Logger logger) {
        printSentCounter(logger);
        pktReceiver.printReceivedPckCounter(logger);
    }

//    public void removeServerPacket(IPacket iPacket) {
//        if (iPacket != null) {
//            int action = iPacket.getAction();
//            long serverPacketId = iPacket.getServerPacketID();
//            if (action > 0 && serverPacketId > 0) {
//                ServerPacket serverPacket = (ServerPacket) resenderPackets.remove(action, serverPacketId);
//                logger.trace("removed  serverPacket { \"action\": " + action + " , \"serverPacketId\" : " + serverPacketId + "}");
//                if (serverPacket != null) {
//                    int sequence = serverPacket.getSequence();
//                    if (sequence > 0) {
//                        if (serverPacket.isBroken()) {
//                            long uniqueKey = serverPacket.getUniqueKey();
//                            missingBrokenPackets.remove(serverPacket.getUniqueKey(), sequence);
//                            logger.trace("removed missing broken serverPacket { \"action\": " + action + " , \"uniquekey\": " + uniqueKey + " , \"seq\" : " + sequence + "}");
//                        }
//                        else {
//                            String clientPacketID = serverPacket.getClientPacketID();
//                            missingFullPackets.remove(serverPacket.getClientPacketID(), sequence);
//                            logger.trace("removed missing full serverPacket { \"action\": " + action + " , \"clientPckId\": " + clientPacketID + " , \"seq\" : " + sequence + "}");
//                        }
//                    }
//                    else {
//                        logger.trace("server packet is not broken or paginated for { \"action\" : " + action + " , \"serverPacketId\" : " + serverPacketId + "}");
//                    }
//                }
//                else {
//                    logger.trace("trying to remove null ServerPacket for { \"action\" : " + action + " , \"serverPacketId\" : " + serverPacketId + "}");
//                }
//            }
//            else {
//                logger.trace("invalid action or server packet id. { \"action\" : " + action + " , \"serverPacketId\" : " + serverPacketId + "}");
//            }
//        }
//        else {
//            logger.trace("trying to remove null serverPacket ");
//        }
//
//    }
}
