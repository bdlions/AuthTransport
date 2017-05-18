package org.bdlions.transport.channel.provider;

import java.net.UnknownHostException;
import java.util.ArrayList;
import org.bdlions.session.ISessionManager;
import org.bdlions.transport.channel.client.ClientChannelImpl;
import org.bdlions.transport.channel.exceptions.ChannelNotFoundException;
import org.bdlions.transport.channel.exceptions.ChannelOpenedException;
import org.bdlions.transport.channel.relay.RelayChannelImpl;
import org.bdlions.transport.packet.factory.ClientPacketFactoryWorker;
import org.bdlions.transport.processor.packet.ClientPacketProcessor;
import org.bdlions.transport.remover.PacketRemover;
import org.bdlions.transport.sender.IClientPacketSender;
import org.bdlions.transport.sender.IRelayPacketSender;
import org.bdlions.util.handler.request.IClientRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author alamgir
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author saikat
 */
public class ChannelProviderImpl implements IChannelProvider {

    private int CHANNEL_SIZE = 100;
    private ArrayList<IClientPacketSender> channels;
    private RelayChannelImpl relayChannel;
    private final IClientRequestHandler handler;
    private final ClientPacketFactoryWorker worker;
    private final ISessionManager sessionManager;
    private int startPort;
    private int endPort;
    private int startChannelId = 1;
    private static PacketRemover packetRemover;
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelProviderImpl.class);

    public ChannelProviderImpl(IClientRequestHandler handler, ISessionManager sessionManager) {
        this.handler = handler;
        this.sessionManager = sessionManager;
        worker = new ClientPacketFactoryWorker();
        this.startPort = 10000;
        this.endPort = 10002;
    }

    @Override
    public ArrayList<IClientPacketSender> getClientChannelList() {
        return channels;
    }

    @Override
    public IClientPacketSender getClientChannel(int i) throws ChannelNotFoundException {
        try {
            IClientPacketSender channel = channels.get(i);
            return channel;
        } catch (Exception e) {
            throw new ChannelNotFoundException();
        }

    }

    @Override
    public IRelayPacketSender getRelayChannel() throws ChannelNotFoundException {
        if (relayChannel == null) {
            throw new ChannelNotFoundException();
        }

        return relayChannel;
    }

    public void start() throws Exception {
        startRelayChannel();
        startClientChannels();
        packetRemover = new PacketRemover(this);
        packetRemover.start();
    }

    public void stop() {
        stopClientChannels();
        worker.shutdown();
        stopRelayChannel();
        packetRemover.stopService();
    }

    private void startClientChannels() throws ChannelOpenedException {
        /**
         * set all the client channels and start
         */
        channels = new ArrayList<>();
//            for (int i = 1; i <= CHANNEL_SIZE; i++) {
        this.CHANNEL_SIZE = endPort - startPort;
        int id = startChannelId;
        for (int i = startPort; i <= endPort; i++) {
//                InetAddress ip = InetAddress.getLocalHost();
            ClientChannelImpl channel = new ClientChannelImpl("Client Channel " + id, i, handler, sessionManager);

            ((ClientPacketProcessor) channel.getPacketProcessor()).setRelayChannel(relayChannel);
            ((ClientPacketProcessor) channel.getPacketProcessor()).setPacketFactoryWorker(worker);

            channel.start();
            channels.add(channel);
            id++;
        }
    }

    private void stopClientChannels() {
        for (int i = 0; channels != null && i < channels.size(); i++) {
            ClientChannelImpl clientChannel = (ClientChannelImpl) channels.get(i);
            clientChannel.stop();
        }
    }

    private void startRelayChannel() throws Exception, UnknownHostException, ChannelOpenedException {
//            InetAddress ip = InetAddress.getLocalHost();
        String updateSQL = "update authservers set ";
        if (startPort <= 0 && startPort >= endPort) {
            throw new Exception("Start Port & End Port aren't properly configured... { startPort:" + startPort + ", endPort:" + endPort + "}");
//                LOGGER.info("Start Port & End Port aren't properly configured... { startPort:" + startPort + ", endPort:" + endPort + "}");
//                System.exit(0);
        }
        int diff = endPort - startPort;
        int relayPort = startPort + diff + 1;
//                relayChannel = new RelayChannelImpl(20101, handler, sessionManager);
        relayChannel = new RelayChannelImpl(relayPort, handler, sessionManager);
        relayChannel.start();
        //updateSQL += "relayPort=" + relayPort + " where serverID=" + ConfigurationManager.getInstance().getServerId() + " and playingRole=" + ConfigurationManager.getInstance().getRole();

        if (relayChannel == null) {
            throw new Exception("Sockets are not initialized properly.\nPlease check your system firewall(port blocking " + startPort + "-" + endPort + ").\n\nSystem is exiting....");
//                LOGGER.error("Sockets are not initialized properly.\nPlease check your system firewall(port blocking " + startPort + "-" + endPort + ").\n\nSystem is exiting....");
//                System.exit(0);
        }

    }

    private void stopRelayChannel() {
        if(relayChannel != null)
            relayChannel.stop();
    }

//    public Object getCurrentSession() {
//        throw new UnsupportedOperationException("Not supported yet!");
//    }
//
//    public Object getSessionByUserId(int id) {
//        throw new UnsupportedOperationException("Not supported yet!");
//    }
    
}
