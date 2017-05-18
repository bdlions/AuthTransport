/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.receiver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saikat
 */
public class PacketReceiver extends Thread {

    private final DatagramSocket datagramSocket;
    private final LinkedBlockingQueue<DatagramPacket> queue;
    private final int BUFFER_SIZE = 1200;
    private static boolean running = true;
    private final Logger logger;
    
    private int receivedCounter = 0;
    public PacketReceiver(DatagramSocket s, LinkedBlockingQueue<DatagramPacket> q) {
        datagramSocket = s;
        queue = q;
        logger = LoggerFactory.getLogger(this.getClass());
        running = true;
    }

    public synchronized DatagramPacket receivePacket() throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, BUFFER_SIZE);
        try{
            datagramSocket.receive(packet);
        }
        catch(Exception ex){
            logger.debug("Socket closed.");
        }
        return packet;
    }

    public void stopService() {
        try {
            running = false;
            datagramSocket.close();
            join();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void run() {
        logger.info("Packet receiver thread started");
        while (running) {
            try {
                if (!datagramSocket.isClosed()) {
                    try {
                        DatagramPacket packet = receivePacket();
                        queue.add(packet);
                    } catch (IOException ex) {
                        logger.error("Exception in socet port " + datagramSocket.getLocalPort() + ex.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.error("Exception in receiving packet ", e);
            }

        }
    }

    public void printReceivedPckCounter(Logger lg){
        lg.trace("received Packet : "+ receivedCounter + " at port : " + datagramSocket.getLocalPort() );
    }
}
