/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet.factory;

import com.auction.util.REQUEST_TYPE;
import java.net.DatagramPacket;
import java.util.concurrent.TimeUnit;
//import org.bdlions.transport.packet.GenericPacket;
import org.bdlions.transport.packet.IPacket;
//import org.ipvision.binary.packet.PacketBuilder;
//import org.ipvision.packets.GenericPacket;
//import org.ipvision.transport.packet.IPacket;
//import org.ringid.constants.SOCKET_TYPE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir
 */
public class ClientPacketFactory implements Runnable {

    private final DatagramPacket datagramPacket;
    private final IClientPacketFactoryCallback callback;
    private final Logger logger = LoggerFactory.getLogger(ClientPacketFactory.class);
    private final Logger summaryLogger = LoggerFactory.getLogger(ClientPacketFactory.class);
    private final long TIMEOUT_TIME = 1000;
    private final long processingStartTime;

    public ClientPacketFactory(DatagramPacket datagramPacket, IClientPacketFactoryCallback callback) {
        this.callback = callback;
        this.datagramPacket = datagramPacket;
        this.processingStartTime = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            long elapsedTime = System.currentTimeMillis() - processingStartTime;
            if (elapsedTime < TIMEOUT_TIME) {
                IPacket socketPacket;
//                REQUEST_TYPE socketType = REQUEST_TYPE.getSocket(datagramPacket.getData()[0]);
//                socketPacket = getParams(socketType);
//            System.out.println("packet : " + socketPacket);
//                if (socketPacket != null) {
//                    callback.receiveSocketPacket(socketType, socketPacket);
//                }
                TimeUnit.MILLISECONDS.sleep(1);
            } else {
                //
                logger.info("Timeout from ClientPacket Factory");
            }
        } catch (Exception e) {
            logger.error("Exception in CLient Packet Factory --> " + e.getMessage(), e);
        }
    }

//    private synchronized GenericPacket getParams(REQUEST_TYPE socket) {
//        logger.trace("Socket type : " + socket);
//        int length = datagramPacket.getLength() - 2;
//        int dataLength;
//        GenericPacket recvParams = null;
//        if (length > 0) {
//            try {
//                byte[] received_data = new byte[datagramPacket.getLength() - 2];
//                System.arraycopy(datagramPacket.getData(), 2, received_data, 0, received_data.length);
//                String recv_data = null;
//                Gson json = new GsonBuilder().serializeNulls().create();
//
//                IPacketHeader header = null;
//                switch (socket) {
//                    case KEEP_ALIVE:
//                    case CONFIRMATION:
//                        break;
//                    case SESSION_LESS:
//                        System.arraycopy(datagramPacket.getData(), 2, received_data, 0, received_data.length);
//                        recv_data = new String(received_data);
//                        summaryLogger.info("receive request -->" + recv_data);
//                        break;
//                    default:
//                        ByteDecoder byteDecoder = new ByteDecoder(received_data, 0);
//                        header = byteDecoder.decode(IPacketHeader.class);
//                        int index = byteDecoder.getDecodedLength();
//                        dataLength = ((received_data[index + 1] & 0xff) << 8) | (received_data[index] & 0xff);
//                        if (dataLength > 0) {
//                            recv_data = new String(received_data, index + 2, dataLength);
//                        }
//                        summaryLogger.info("received request -->" + System.lineSeparator() + "HEADER : " + json.toJson(header) + System.lineSeparator() + "DATA : " + recv_data);
//                }
//                switch (socket) {
//                    case AUTH: {
//                        recvParams = new AuthParameters();
//                        if (recv_data != null) {
//                            if (header.getAction() == AppConstants.ACTION_ADD_PROFILE_DETAILS) {
//                                recvParams = new UserParams();
//                                recvParams = json.fromJson(recv_data.trim(), UserParams.class);
//                            } else {
//                                recvParams = json.fromJson(recv_data.trim(), AuthParameters.class);
//                            }
//                        }
//                        recvParams.setSocketType(SOCKET_TYPE.AUTH);
//                        break;
//                    }
//                    case CALL: {
//                        recvParams = new CallParameters();
//                        if (recv_data != null) {
//                            recvParams = json.fromJson(recv_data.trim(), CallParameters.class);
//                        }
//                        recvParams.setSocketType(SOCKET_TYPE.CALL);
//                        break;
//                    }
//                    case CHAT: {
//                        recvParams = new ChatParameters();
//                        if (recv_data != null) {
//                            recvParams = json.fromJson(recv_data.trim(), ChatParameters.class);
//                        }
//                        recvParams.setSocketType(SOCKET_TYPE.CHAT);
//                        break;
//                    }
//                    case REQUEST: {
//                        recvParams = new RequestParameters();
//                        if (recv_data != null) {
//                            recvParams = json.fromJson(recv_data.trim(), RequestParameters.class);
//                        }
//                        recvParams.setSocketType(SOCKET_TYPE.REQUEST);
//                        break;
//                    }
//                    case UPDATE: {
//                        recvParams = new UpdateParameters();
//                        if (recv_data != null) {
//                            recvParams = json.fromJson(recv_data.trim(), UpdateParameters.class);
////                        if (recvParams.getAction() == AppConstants.ACTION_ADD_PROFILE_IMAGE) {
////                            recvParams = new NewsFeed();
////                            recvParams = json.fromJson(recv_data.trim(), NewsFeed.class);
////                        }
//                        }
//                        recvParams.setSocketType(SOCKET_TYPE.UPDATE);
//                        break;
//                    }
//                    case KEEP_ALIVE: {
//                        logger.trace("keep alive request : { \"sessionId\" : " + recv_data + "}");
//                        recvParams = new GenericPacket();
//                        String sessionId = new String(received_data);
//                        recvParams.setSessionId(sessionId);
//                        recvParams.setSocketType(SOCKET_TYPE.KEEP_ALIVE);
//                        break;
//                    }
//                    case CONFIRMATION: {
//                        recvParams = new GenericPacket();
//                        received_data = datagramPacket.getData();
//                        int action = (received_data[2] & 0xFF) << 8 | (received_data[3] & 0xFF);
//                        long serverPacketId = 0;
////                        int index = 8;
////                        for (int i = 7; i > -1; i--) {
////                            serverPacketId |= (received_data[index++] & 0xFFL) << (8 * i);
////                        }
//                        serverPacketId = (received_data[8] & 0xFF) << 24 | (received_data[9] & 0xFF) << 16 | (received_data[10] & 0xFF) << 8 | (received_data[11] & 0xFF);
//                        int sessionLength = datagramPacket.getLength() - 12;
//                        byte[] sessionIdByte = new byte[sessionLength];
//                        System.arraycopy(datagramPacket.getData(), 12, sessionIdByte, 0, sessionLength);
//                        String sessionId = new String(sessionIdByte);
//                        logger.trace("confirmation request : {\"action\" : " + action + " , \"serverPacketId\" : " + serverPacketId + " , \"sessionId\" : " + sessionId + "}");
//                        recvParams.setAction(action);
//                        recvParams.setServerPacketID(serverPacketId);
//                        recvParams.setSessionId(sessionId);
//                        recvParams.setSocketType(SOCKET_TYPE.CONFIRMATION);
//                        break;
//                    }
//                    case COMMAND: {
//                        logger.trace("receive request : " + recv_data);
//                        recvParams = new CommandParameters();
//                        if (recv_data != null) {
//                            recvParams = json.fromJson(recv_data.trim(), CommandParameters.class);
//                        }
//                        recvParams.setSocketType(SOCKET_TYPE.COMMAND);
//                        break;
//                    }
//                    case SESSION_LESS: {
//                        summaryLogger.info("receive request : " + recv_data);
//                        recvParams = new SessionLessParams();
//                        if (recv_data != null) {
//                            recvParams = json.fromJson(recv_data.trim(), SessionLessParams.class);
//                        }
//                        recvParams.setSocketType(SOCKET_TYPE.SESSION_LESS);
//                        break;
//                    }
//                    default: {
//                        break;
//                    }
//                }
//                if (recvParams != null && header != null) {
//                    recvParams.setAction(header.getAction());
//                    recvParams.setClientPacketID(header.getClientPacketId());
//                    recvParams.setSessionId(header.getSessionId());
//                }
////                recvParams.setData(received_data);
//            } catch (Exception e) {
//                logger.error("error in client request parsing for request :{ " + new String(datagramPacket.getData())+"} " + e.getMessage(), e);
//
//                try {
//                    byte[] received_data = new byte[datagramPacket.getLength() - 2];
//                    System.arraycopy(datagramPacket.getData(), 2, received_data, 0, received_data.length);
//                    String recv_data = new String(received_data);
//                    Gson json = new GsonBuilder().serializeNulls().create();
//                    switch (socket) {
//                        case AUTH: {
//                            summaryLogger.info("receive request : " + recv_data);
//                            recvParams = new AuthParameters();
//                            recvParams = json.fromJson(recv_data.trim(), AuthParameters.class);
//                            if (recvParams.getAction() == AppConstants.ACTION_ADD_PROFILE_DETAILS) {
//                                recvParams = new UserParams();
//                                recvParams = json.fromJson(recv_data.trim(), UserParams.class);
//                            }
//                            recvParams.setSocketType(SOCKET_TYPE.AUTH);
//                            break;
//                        }
//                        case CALL: {
//                            summaryLogger.info("receive request : " + recv_data);
//                            recvParams = new CallParameters();
//                            recvParams = json.fromJson(recv_data.trim(), CallParameters.class);
//                            recvParams.setSocketType(SOCKET_TYPE.CALL);
//                            break;
//                        }
//                        case CHAT: {
//                            summaryLogger.info("receive request : " + recv_data);
//                            recvParams = new ChatParameters();
//                            recvParams = json.fromJson(recv_data.trim(), ChatParameters.class);
//                            recvParams.setSocketType(SOCKET_TYPE.CHAT);
//                            break;
//                        }
//                        case REQUEST: {
//                            summaryLogger.info("receive request : " + recv_data);
//                            recvParams = new RequestParameters();
//                            recvParams = json.fromJson(recv_data.trim(), RequestParameters.class);
//                            recvParams.setSocketType(SOCKET_TYPE.REQUEST);
//                            break;
//                        }
//                        case UPDATE: {
//                            summaryLogger.info("receive request : " + recv_data);
//                            recvParams = new UpdateParameters();
//                            recvParams = json.fromJson(recv_data.trim(), UpdateParameters.class);
////                        if (recvParams.getAction() == AppConstants.ACTION_ADD_PROFILE_IMAGE) {
////                            recvParams = new NewsFeed();
////                            recvParams = json.fromJson(recv_data.trim(), NewsFeed.class);
////                        }
//                            recvParams.setSocketType(SOCKET_TYPE.UPDATE);
//                            break;
//                        }
//                        case KEEP_ALIVE: {
//                            logger.trace("keep alive request : { \"sessionId\" : " + recv_data + "}");
//                            recvParams = new GenericPacket();
//                            String sessionId = new String(received_data);
//                            recvParams.setSessionId(sessionId);
//                            recvParams.setSocketType(SOCKET_TYPE.KEEP_ALIVE);
//                            break;
//                        }
//                        case CONFIRMATION: {
//                            recvParams = new GenericPacket();
//                            received_data = datagramPacket.getData();
//                            int action = (received_data[2] & 0xFF) << 8 | (received_data[3] & 0xFF);
//                            long serverPacketId = 0;
////                        int index = 8;
////                        for (int i = 7; i > -1; i--) {
////                            serverPacketId |= (received_data[index++] & 0xFFL) << (8 * i);
////                        }
//                            serverPacketId = (received_data[8] & 0xFF) << 24 | (received_data[9] & 0xFF) << 16 | (received_data[10] & 0xFF) << 8 | (received_data[11] & 0xFF);
//                            int sessionLength = datagramPacket.getLength() - 12;
//                            byte[] sessionIdByte = new byte[sessionLength];
//                            System.arraycopy(datagramPacket.getData(), 12, sessionIdByte, 0, sessionLength);
//                            String sessionId = new String(sessionIdByte);
//                            logger.trace("confirmation request : {\"action\" : " + action + " , \"serverPacketId\" : " + serverPacketId + " , \"sessionId\" : " + sessionId + "}");
//                            recvParams.setAction(action);
//                            recvParams.setServerPacketID(serverPacketId);
//                            recvParams.setSessionId(sessionId);
//                            recvParams.setSocketType(SOCKET_TYPE.CONFIRMATION);
//                            break;
//                        }
//                        case COMMAND: {
//                            logger.trace("receive request : " + recv_data);
//                            recvParams = new CommandParameters();
//                            recvParams = json.fromJson(recv_data.trim(), CommandParameters.class);
//                            recvParams.setSocketType(SOCKET_TYPE.COMMAND);
//                            break;
//                        }
//                        case SESSION_LESS: {
//                            summaryLogger.info("receive request : " + recv_data);
//                            recvParams = new SessionLessParams();
//                            recvParams = json.fromJson(recv_data.trim(), SessionLessParams.class);
//                            recvParams.setSocketType(SOCKET_TYPE.SESSION_LESS);
//                            break;
//                        }
//                        default: {
//                            break;
//                        }
//                    }
//
////                recvParams.setData(received_data);
//                } catch (Exception ex) {
//                    logger.error("error in client request parsing " + ex.getMessage(), ex);
//                }
//            }
//        }
//
//        if (recvParams != null) {
//            recvParams.setClientAddress(datagramPacket.getAddress());
//            recvParams.setClientPort(datagramPacket.getPort());
//        }
//        return recvParams;
//    }

}
