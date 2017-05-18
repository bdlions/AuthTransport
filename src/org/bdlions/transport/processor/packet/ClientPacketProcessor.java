/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.processor.packet;

import com.auction.commons.ClientMessages;
import com.auction.dto.response.ClientFailedResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bdlions.session.ISession;
import org.bdlions.session.ISessionManager;
import org.bdlions.transport.packet.IPacket;
import org.bdlions.transport.packet.analyzer.ClientDataPacketAnalyzer;
import org.bdlions.transport.packet.factory.ClientPacketFactory;
import org.bdlions.transport.packet.factory.ClientPacketFactoryWorker;
import org.bdlions.transport.packet.factory.IClientPacketFactoryCallback;
import org.bdlions.transport.packet.analyzer.IDataPacketAnalyzingCallback;
import org.bdlions.transport.channel.IChannel;
import org.bdlions.transport.channel.client.ClientChannelImpl;
import org.bdlions.transport.packet.PacketImpl;
import org.bdlions.transport.repository.HierarchicalRepository;
import org.bdlions.transport.sender.IClientPacketSender;
import org.bdlions.transport.sender.IRelayPacketSender;
import org.bdlions.util.handler.request.IClientRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 *
 * @author saikat
 */
public class ClientPacketProcessor implements IPacketProcessor {

    private final IChannel clientChannel;
    private IChannel relayChannel;
    private final IClientRequestHandler handler;
    private final ClientDataPacketAnalyzer clientDataPacketAnalyzer;
    private ClientPacketFactoryWorker factoryWorker;
    private final Logger logger;
    private DatagramPacket received_packet;
    private final ISessionManager sessionManager;
    private Gson gson = new GsonBuilder().create();

    public ClientPacketProcessor(IChannel clientChannel, IClientRequestHandler handler, ISessionManager sessionManager) {
        this.clientChannel = clientChannel;
        this.handler = handler;
        clientDataPacketAnalyzer = new ClientDataPacketAnalyzer();
        this.sessionManager = sessionManager;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public void setPacketFactoryWorker(ClientPacketFactoryWorker factoryWorker) {
        this.factoryWorker = factoryWorker;
    }

    @Override
    public synchronized void process(DatagramPacket packet) {
        this.received_packet = packet;
        

        /**
         * Process the packet and put in the received packetRepository
         *
         */
        clientDataPacketAnalyzer.analyzePacket(packet, new IDataPacketAnalyzingCallback() {
            InetAddress remoteAddress = packet.getAddress();
            int remotePort = packet.getPort();
            
            @Override
            public void emptyPacket() {
                logger.error("Empty packet received.");
            }

            @Override
            public void incorrectPacketFormat() {
                logger.error("Invalid packet format received.");
            }

            @Override
            public void incorrectSocketType() {
                logger.error("Invalid packet type received.");
            }

            @Override
            public void receiveFullPacket(IPacket packet) {
                PacketImpl packetImpl = (PacketImpl) packet;
                packetImpl.setRemoteIP(remoteAddress);
                packetImpl.setRemotePort(remotePort);
                packetImpl.setClientPacketSender((IClientPacketSender)clientChannel);
                
                ISession session = sessionManager.getSessionBySessionId(packet.getPacketHeader().getSessionId());
                try {
                    String response = gson.toJson(handler.executeRequest(packet));                    
                    packet.setResponseData(response.getBytes());
                    packet.getClientPacketSender().send(session, packet);
                } catch (Throwable ex) {
                    ClientFailedResponse cr = new ClientFailedResponse();
                    cr.setMessage(ClientMessages.REQUEST_DID_NOT_PROCESSED_SUCCESSFULLY);
                    String response = gson.toJson(cr);
                    packet.setResponseData(response.getBytes());
                    packet.getClientPacketSender().send(session, packet);
                    ex.printStackTrace();
                }
            }
        });
        
//        clientDataPacketAnalyzer.analyzePacket(received_packet, new IDataPacketAnalyzingCallback() {
//
//            @Override
//            public void emptyPacket() {
//                //discard because packet is empty
//                logger.info("Empty packet received from { \"ip\": " + received_packet.getAddress().getHostAddress() + ", \"port\": " + received_packet.getPort() + "}");
//            }
//
//            @Override
//            public void incorrectPacketFormat() {
//                //packet format is empty so discard
//                logger.info("Empty packet received from { \"ip\": " + received_packet.getAddress().getHostAddress() + ", \"port\": " + received_packet.getPort() + "}");
//            }
//
//            @Override
//            public void incorrectSocketType() {
//                //socket type is incorrect so discard
//                logger.info("Empty packet received from { \"ip\": " + received_packet.getAddress().getHostAddress() + ", \"port\": " + received_packet.getPort() + "}");
//            }
//
//            @Override
//            public void receiveBrokenPacket(InetAddress clientIP, int clientPort, BrokenPacketAttributes brokenPckAttr) {
//                //broken packet so send confirmation only
//                logger.trace("broken packet received from { \"ip\": " + received_packet.getAddress().getHostAddress() + ", \"port\": " + received_packet.getPort() + "}");
//                sendConfirmation(brokenPckAttr.getClientPacketID());
//            }
//
//            @Override
//            public void receiveFullPacket(DatagramPacket datagramFullPacket) {
//                factoryWorker.doWork(new ClientPacketFactory(datagramFullPacket, new IClientPacketFactoryCallback() {
//                    @Override
//                    public void receiveSocketPacket(SOCKET_TYPE socketType, IPacket packet) {
//                        /**
//                         * taking the packet if it is already processed then
//                         * stop processing
//                         */
//                        final GenericPacket genericPacket = (GenericPacket) packet;
////                        System.out.println("client packet id " + genericPacket.getClientPacketID());
//                        genericPacket.setClientPacketSender((IClientPacketSender) clientChannel);
//                        genericPacket.setRelayPacketSender((IRelayPacketSender) relayChannel);
//                        IClientPacketSender clientPacketSender = (IClientPacketSender) clientChannel;
//
//                        logger.trace("received request from {\"ip\" : " + packet.getClientAddress().getHostAddress() + ", \"port\" : " + packet.getClientPort() + " , \"action\" : " + packet.getAction() + " , \"socket\" : " + packet.getSocketType() + "}");
//                        /**
//                         * *
//                         * **********************************************************************
//                         * For saikat Implement missing and missing broken
//                         * packet replied/processed checking
//                         * **********************************************************************
//                         */
////                        if (packet.getSocketType() != SOCKET_TYPE.AUTH && packet.getSocketType() != SOCKET_TYPE.CONFIRMATION && packet.getSocketType() != SOCKET_TYPE.KEEP_ALIVE) {
////                            String sessionID = packet.getSessionId();
////                            if (sessionID != null) {
////                                if (sessionManager.getSessionBySessionId(sessionID) == null) {
////                                    loginCheck(genericPacket);
////                                    return;
////                                }
////                            } else {
////                                loginCheck(genericPacket);
////                                return;
////                            }
////
////                        }
//                        switch (packet.getSocketType()) {
//                            case AUTH: {
//                                switch (packet.getAction()) {
//                                    case AppConstants.ACTION_SIGN_OUT:
//                                    case AppConstants.ACTION_CHANGE_MOOD:
//                                    case AppConstants.ACTION_CHANGE_LIVE_STATUS: {
//                                        String sessionID = packet.getSessionId();
//                                        if (sessionID != null) {
//                                            if (sessionManager.getSessionBySessionId(sessionID) == null) {
//                                                loginCheck(genericPacket);
//                                                return;
//                                            }
//                                        }
//                                        else {
//                                            loginCheck(genericPacket);
//                                            return;
//                                        }
//                                        break;
//                                    }
//                                    default:
//                                        break;
//                                }
//                            }
//                            case CONFIRMATION:
//                            case KEEP_ALIVE:
//                            case SESSION_LESS:
//                                break;
//                            default:
//                                String sessionID = packet.getSessionId();
//                                if (sessionID != null) {
//                                    if (sessionManager.getSessionBySessionId(sessionID) == null) {
//                                        loginCheck(genericPacket);
//                                        return;
//                                    }
//                                }
//                                else {
//                                    loginCheck(genericPacket);
//                                    return;
//                                }
//                        }
//
//                        if (packet.getSocketType() == SOCKET_TYPE.KEEP_ALIVE) {
//                            receiveSessionAlivePacket(packet);
//                            return;
//                        }
//
//                        if (packet.getSocketType() == SOCKET_TYPE.CONFIRMATION) {
//                            receiveConfirmationPacket(packet);
//                            return;
//                        }
//                        String clientPacketId = genericPacket.getClientPacketID();
//                        if (clientPacketId != null) {
//                            int action = packet.getAction();
//                            if (action == AppConstants.ACTION_GET_BROKEN_MISSING_PACKETS || action == AppConstants.ACTION_GET_BROKEN_MISSING_PACKETS) {
//                                if (packet.getSocketType() == SOCKET_TYPE.REQUEST) {
//                                    RequestParameters requestParameters = (RequestParameters) packet;
//                                    sendConfirmation(clientPacketId);
//                                    List<Integer> sequences = requestParameters.getSequences();
//                                    if (sequences != null && !sequences.isEmpty()) {
//
//                                        switch (action) {
//                                            case AppConstants.ACTION_GET_MISSING_PACKETS: {
//                                                HierarchicalRepository missingFullPacketRepo = clientPacketSender.getMissingFullPackRepository();
//                                                for (int sequence : sequences) {
//                                                    ServerPacket missingFullPacket = (ServerPacket) missingFullPacketRepo.get(clientPacketId, sequence);
//                                                    if (missingFullPacket != null) {
//                                                        sendMissingPacket(missingFullPacket, clientPacketSender);
//                                                    } else {
//                                                        logger.info("requested missing full packet is null for { \"clientPckId\" : " + clientPacketId + " , \"sequence\" : " + sequence + " , \"action\" : " + packet.getAction() + "}");
//                                                    }
//                                                }
//                                                break;
//                                            }
//                                            case AppConstants.ACTION_GET_BROKEN_MISSING_PACKETS: {
//                                                HierarchicalRepository missingBrknPacketRepo = clientPacketSender.getMissingBrokenPackRepository();
//                                                int clientUniqueKey = requestParameters.getUniqueKey();
//                                                if (clientUniqueKey > 0) {
//                                                    for (int sequence : sequences) {
//                                                        ServerPacket missingBrknPacket = (ServerPacket) missingBrknPacketRepo.get(clientUniqueKey, sequence);
//                                                        if (missingBrknPacket != null) {
//                                                            sendMissingPacket(missingBrknPacket, clientPacketSender);
//                                                        } else {
//                                                            logger.info("requested missing broken packet is null for { \"uniquekey\" : " + clientUniqueKey + " , \"sequence\" " + sequence + " , \"action\" : " + packet.getAction() + "}");
//                                                        }
//                                                    }
//                                                } else {
//                                                    logger.info("unique key is invalid for requested brokenPacket . { \"action\" : " + action + "} from { \"ip\" : " + packet.getClientAddress().getHostAddress() + " , \"port\" : " + packet.getClientPort() + "}");
//                                                }
//                                                break;
//                                            }
//                                        }
//                                    } else {
//                                        logger.error("sequence list is null for missing packet request.  { \"action\" : " + action + "} from { \"ip\" : " + packet.getClientAddress().getHostAddress() + " , \"port\" : " + packet.getClientPort() + "}");
//                                    }
//                                } else {
//                                    logger.error("socket type must be REQUEST type for missing packet request");
//                                }
//                            }
//                            else if (clientPacketSender.getRepliedPacketRepository() != null) {
//                                if (!clientPacketSender.getRepliedPacketRepository().containsKey(clientPacketId)) {
//                                    String token = null;
//                                    if (packet.getSocketType() == SOCKET_TYPE.AUTH || packet.getSocketType() == SOCKET_TYPE.COMMAND || packet.getSocketType() == SOCKET_TYPE.SESSION_LESS) {
////                                        AuthParameters parameters = (AuthParameters) packet;
////                                        if (parameters.getUserName() != null) {
////                                            token = parameters.getUserName();
////                                        } else {
////                                            ISession session = null;
////                                            String sessionId = parameters.getSessionId();
////                                            if (sessionId != null) {
////                                                session = sessionManager.getSessionBySessionId(sessionId);
////                                            } else if (parameters.getUserTableID() > 0) {
////                                                session = sessionManager.getOnlineSessionByUserId(parameters.getUserTableID());
////                                            }
////                                            if (session != null) {
////                                                token = session.getUserName();
////                                            } else {
////                                                logger.debug("Session is null for " + packet.getClientAddress() + ":" + packet.getClientPort());
////                                            }
////                                        }
//                                        if (packet.getSessionId() != null) {
//                                            sessionManager.updateSession(packet.getSessionId(), packet.getClientAddress(), packet.getClientPort());
//                                        }
//                                        handler.receiveRequest(genericPacket);
//                                        logger.trace("pass object to handler. {\"action\" : " + genericPacket.getAction() + ", \"socket\" : " + packet.getSocketType() + "}");
//                                    } else {
//                                        String session_id = packet.getSessionId();
//                                        if (session_id != null) {
//                                            ISession session = sessionManager.getSessionBySessionId(session_id);
//                                            if (session != null) {
//                                                token = session.getUserName();
//                                            } else {
//                                                logger.error("Session is null for " + packet.getClientAddress().getHostAddress() + ":" + packet.getClientPort());
//                                            }
//                                        } else {
//                                            logger.error("Session Id is null for client " + packet.getClientAddress().getHostAddress() + ":" + packet.getClientPort());
//                                        }
//                                        if (token != null) {
//                                            if (((IClientPacketSender) clientChannel).isRequestProcessing(token, packet.getAction())) {
//                                                handler.receiveRequest(genericPacket);
//                                                sessionManager.updateSession(packet.getSessionId(), packet.getClientAddress(), packet.getClientPort());
//                                                logger.trace("pass object to handler. {\"action\" : " + genericPacket.getAction() + ", \"socket\" : " + packet.getSocketType() + "}");
//                                            } else {
//                                                logger.trace("request is going to be processed.. {\"action\" : " + genericPacket.getAction() + ", \"socket\" : " + packet.getSocketType() + "}");
//                                                return;
//                                            }
//                                        } else {
//                                            logger.error("token is null for {\"action\" : " + genericPacket.getAction() + ", \"socket\" : " + packet.getSocketType() + "}");
//                                            return;
//                                        }
//
//                                    }
//
//                                } else {
//                                    logger.trace("response packet has already generated for {\"clientPckId\" : " + clientPacketId + ", \"action\" : " + genericPacket.getAction() + "}");
//                                    IPacket repliedPacket = (IPacket) clientPacketSender.getRepliedPacketRepository().get(clientPacketId);
//                                    if (repliedPacket != null) {
//                                        clientPacketSender.reSend(repliedPacket);
//                                    } else {
//                                        logger.error("Replied packet is null for {\"clientPckId\" : " + clientPacketId + ", \"action\" : " + genericPacket.getAction() + "}");
//                                    }
//                                }
////                                logger.trace("result port: " + genericPacket.getClientPort());
//                            } else {
//                                logger.error("Replied Packet Repository is null");
//                            }
//                        }
//                        else {
//                            logger.error("client packet ID is null..");
//                        }
//                    }
//
//                    @Override
//                    public void receiveSessionAlivePacket(IPacket iPacket) {
//                        logger.trace("keep alive request {\"sessionID\"  : " + iPacket.getSessionId() + "} from " + iPacket.getClientAddress().getHostAddress() + ":" + iPacket.getClientPort());
//                        sessionManager.updateSession(iPacket.getSessionId(), iPacket.getClientAddress(), iPacket.getClientPort());
//                    }
//
//                    @Override
//                    public void receiveConfirmationPacket(IPacket iPacket) {
//                        logger.trace("confirmation reply {\"action\": " + iPacket.getAction() + " , \"serverPacketId\":" + iPacket.getServerPacketID() + "} from " + iPacket.getClientAddress().getHostAddress() + ":" + iPacket.getClientPort());
//                        if (clientChannel instanceof ClientChannelImpl) {
//                            ClientChannelImpl channelImpl = (ClientChannelImpl) clientChannel;
//                            channelImpl.removeServerPacket(iPacket);
//                            String sessionId = iPacket.getSessionId();
//                            if (sessionId != null) {
//                                sessionManager.updateSession(sessionId, iPacket.getClientAddress(), iPacket.getClientPort());
//                            } else {
//                                logger.error("Session Id is null in confirmation packet {\"action\": " + iPacket.getAction() + " , \"serverPacketId\":" + iPacket.getServerPacketID() + "} from " + iPacket.getClientAddress().getHostAddress() + ":" + iPacket.getClientPort());
//                            }
//                        }
//                        else {
//                            logger.error("clientChannel object is not instance of ClientChannelImpl class");
//                        }
//                    }
//                }
//                )
//                );
//            }
//
//        });

        //logger.trace("Client packet received from ip: " + packet.getAddress().getHostAddress() + ", port: " + packet.getPort() + " is processed.");
    }

    public void setRelayChannel(IChannel relayChannel) {
        this.relayChannel = relayChannel;
    }

    private void sendConfirmation(final String client_packet_id) {
//        try {
//            //        PacketBuilder builder = new PacketBuilder();
//            final ISession iSession = getAnonymousSession();
////        builder.setSuccess(true);
////        builder.makePacket(AppConstants.ACTION_CONFIRMATION, 0, 0, client_packet_id, 0, false, (IClientPacketSender) clientChannel, iSession);
//
//            PacketHeaderImpl header = PacketHeaderFactory.getPacketHeader(PACKET_HEADER_TYPE.FULL_PACKET_HEADER);
//            header.setAction(AppConstants.ACTION_CONFIRMATION);
//            header.setClientPacketId(client_packet_id);
//            header.setSuccess(true);
//            new org.ipvision.binary.packet.PacketBuilder(header, new IPacketBuilderCallBack() {
//                @Override
//                public void receiveFullPacket(DatagramPacket packet) {
//                    GenericPacket genericPacket = new GenericPacket();
//                    genericPacket.setClientAddress(iSession.getRemoteIP());
//                    genericPacket.setClientPort(iSession.getRemotePort());
//                    genericPacket.setData(packet.getData());
//                    genericPacket.setClientPacketID(client_packet_id);
//                    genericPacket.setSuccess(true);
//                    genericPacket.setSentTime(System.currentTimeMillis());
//                    iSession.getClientChannel().send(iSession, genericPacket);
//                }
//
//                @Override
//                public void receiveBrokenPacket(DatagramPacket packet, int serverPacketID, IBrokenPacketHeader brokenPacketHeader) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                }
//            }).build();
//            logger.trace("confirmation packet has been sent to " + iSession.getRemoteIP() + ":" + iSession.getRemotePort());
//        } catch (PacketBuildException ex) {
//            logger.error(ex.getLocalizedMessage(), ex);
//        }
    }

//    private void sendMissingPacket(ServerPacket missingPacket, IClientPacketSender clientPacketSender) {
//        ArrayList<ISession> sessionList = sessionManager.getSessions(missingPacket.getUserID());
//        if (sessionList != null) {
//            for (ISession iSession : sessionList) {
//                if (iSession != null && iSession.getLiveStatus() == AppConstants.ONLINE) {
//                    missingPacket.setSuccess(false);
//                    clientPacketSender.send(iSession, missingPacket);
//                } else if (iSession == null) {
//                    logger.error("client session is null for user id : " + missingPacket.getUserID());
//                } else if (iSession.getLiveStatus() != AppConstants.ONLINE) {
//                    logger.error("user : " + missingPacket.getUserID() + " is not online");
//                }
//            }
//        } else {
//            logger.error("session list is null for user id : " + missingPacket.getUserID());
//        }
//    }
//
//    private void loginCheck(GenericPacket genericPacket) {
//        try {
//            //        PacketBuilder packetBuilder = new PacketBuilder();
//            FeedBack feedBack = new FeedBack();
//            feedBack.setSuccess(false);
//            feedBack.setMessage(AppConstants.PLEASE_LOGIN);
////        packetBuilder.setSuccess(false);
////        packetBuilder.addString(AttributeCodes.MESSAGE, AppConstants.PLEASE_LOGIN);
//            final ISession iSession = getAnonymousSession();
////            packetBuilder.makePacket(genericPacket.getAction(), genericPacket.getDevice(), 0, genericPacket.getClientPacketID(), genericPacket.getTabID(), false, iSession.getClientChannel(), iSession);
//            PacketHeaderImpl packetHeaderImpl = PacketHeaderFactory.getPacketHeader(PACKET_HEADER_TYPE.FULL_PACKET_HEADER);
//            packetHeaderImpl.setAction(AppConstants.ACTION_INVALID_LOGIN_SESSION);
//            packetHeaderImpl.setClientPacketId(genericPacket.getClientPacketID());
//            packetHeaderImpl.setSuccess(false);
//            new org.ipvision.binary.packet.PacketBuilder(packetHeaderImpl, new IPacketBuilderCallBack() {
//                @Override
//                public void receiveFullPacket(DatagramPacket packet) {
//                    GenericPacket genericPacket = new GenericPacket();
//                    genericPacket.setData(packet.getData());
//                    genericPacket.setClientAddress(iSession.getRemoteIP());
//                    genericPacket.setClientPort(iSession.getRemotePort());
//                    genericPacket.setSentTime(System.currentTimeMillis());
//                    iSession.getClientChannel().send(iSession, genericPacket);
//                }
//
//                @Override
//                public void receiveBrokenPacket(DatagramPacket packet, int serverPacketID, IBrokenPacketHeader brokenPacketHeader) {
//                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//                }
//            }).build(feedBack);
//        }
//        catch (PacketBuildException ex) {
//            logger.error(ex.getLocalizedMessage(), ex);
//        }
//    }

//    private ISession getAnonymousSession() {
//        UserSession session = new UserSession();
//        session.setClientChannel((IClientPacketSender) clientChannel);
//        session.setRelayChannel((IRelayPacketSender) relayChannel);
//        session.setRemoteIP(received_packet.getAddress());
//        session.setRemotePort(received_packet.getPort());
//
//        return session;
//    }

}
