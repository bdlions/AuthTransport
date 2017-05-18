/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.processor.queue;

import java.net.DatagramPacket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.bdlions.transport.processor.packet.IPacketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saikat
 */
public class QueueProcessorImpl extends Thread implements IQueueProcessor {

    private static boolean running = true;
    private final IPacketProcessor pktProcessor;
    private final BlockingQueue<DatagramPacket> queue;
    private final Logger logger;

    public QueueProcessorImpl(BlockingQueue queue, IPacketProcessor packetProcessor) {
        this.pktProcessor = packetProcessor;
        this.queue = queue;
        logger = LoggerFactory.getLogger(this.getClass());
        running = true;
    }

    @Override
    public synchronized DatagramPacket deQueue() {
        return queue.poll();
    }

    @Override
    public void getMergedPacket() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        logger.info("QueueProcessor started");
        while (running) {
            try {
                while (!queue.isEmpty()) {
                    DatagramPacket packet = deQueue();
                    logger.trace("Packet dequed for processing from ip: " + (packet.getAddress() == null ? "" : packet.getAddress().getHostAddress()) + ", port: " + packet.getPort());
                    pktProcessor.process(packet);
                }
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (NullPointerException e) {
                logger.error(e.getMessage(), e);
            } catch (InterruptedException ie) {
                logger.error(ie.getMessage(), ie);
            }
            catch(Exception ex){
                logger.error(ex.getMessage(), ex);
            }

        }
    }

    public void stopService() {
        try {
            running = false;
            join();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

}
