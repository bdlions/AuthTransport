/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.channel;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import org.bdlions.transport.receiver.PacketReceiver;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;
import org.bdlions.session.ISessionManager;
import org.bdlions.transport.channel.exceptions.ChannelOpenedException;
import org.bdlions.transport.processor.packet.IPacketProcessor;
import org.bdlions.transport.processor.queue.QueueProcessorImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saikat
 */
public abstract class AbstractChannel implements IChannel {

    private final int port;
    private final String channelId;
    private DatagramSocket socket;
    public PacketReceiver pktReceiver;
    private QueueProcessorImpl queueProcessor;
    private final ISessionManager sessionManager;
    private final Logger logger;
    private LinkedBlockingQueue<DatagramPacket> packetProcessingQueue;
    private IPacketProcessor packetProcessor;
    private int sentCounter;

    public AbstractChannel(String channelId, int port, ISessionManager sessionManager) {
        this.channelId = channelId;
        this.port = port;
        this.sessionManager = sessionManager;
        logger = LoggerFactory.getLogger(this.getClass());
    }

    public String getId() {
        return channelId;
    }

    @Override
    public void start() throws ChannelOpenedException {
        try {
            socket = new DatagramSocket(port);

            packetProcessingQueue = getPacketProcessingQueue();
            packetProcessor = getPacketProcessor();

            if (packetProcessingQueue != null && packetProcessor != null) {
                pktReceiver = new PacketReceiver(socket, packetProcessingQueue);
                pktReceiver.setName("Receiver-" + channelId);
                pktReceiver.start();

                queueProcessor = new QueueProcessorImpl(packetProcessingQueue, packetProcessor);
                queueProcessor.setName("QueueProcessor-" + channelId);
                queueProcessor.start();
            } else {
                logger.trace("channel id : " + channelId + " will not receive any data. it can only send.");
            }

        } catch (SocketException se) {
            logger.error(se.getMessage(),se);
            throw new ChannelOpenedException(channelId + " cannot be opened.");
        }
        logger.info(channelId + " started.");
    }

    @Override
    public void stop() {
        socket.close();
        pktReceiver.stopService();
        queueProcessor.stopService();
        logger.info(channelId + " stopped.");
    }

    @Override
    public void send(byte[] data, InetAddress clientIP, int clientPort) {
        try {
            if (socket != null && !socket.isClosed()) {
                int length = data.length;
                DatagramPacket packet = new DatagramPacket(data, length, clientIP, clientPort);
                socket.send(packet);
                
                sentCounter ++;
                logger.trace("sending packet to client address "  + clientIP.getHostName()+":"+clientPort + " from channel : " + getId() + " through " + getIP().getHostAddress() + ":" + getPort());
            }
        } catch (Exception e) {
            logger.error("error in sending packet in " + getId() + " --> "+ e.getMessage(), e);
        }

    }

    @Override
    public InetAddress getIP() {
        try{
            return InetAddress.getLocalHost();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int getPort() {
        return port;
    }

    public ISessionManager getSessionManager() {
        return sessionManager;
    }

//    public abstract void clearTimeoutPackets(Logger l);
    public abstract void clearTimeoutPackets();

    public abstract void resendPackets();

    public abstract LinkedBlockingQueue getPacketProcessingQueue();

    public abstract IPacketProcessor getPacketProcessor();
    
    public void printSentCounter(Logger lg){
        lg.trace("sent Packet : "+ sentCounter + " at port : " + socket.getLocalPort() );
    }
}
