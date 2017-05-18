/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet.analyzer;

import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;
import org.bdlions.transport.repository.HierarchicalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author saikat
 */
public class RelayPacketAnalyzer {

    private final HierarchicalRepository<String, Integer, byte[]> brknRepository;
    private static final int BROKEN_PCK_TIMEOUT = 40000;
    private final Logger logger = LoggerFactory.getLogger(RelayPacketAnalyzer.class);

    public RelayPacketAnalyzer() {
        this.brknRepository = new HierarchicalRepository<>(1000, BROKEN_PCK_TIMEOUT);
    }

    /**
     * **
     * anylize the packet if the packet is full,broken or incorrect format
     *
     * @param datagramPacket
     * @param callback
     */
    public void analyzePacket(DatagramPacket datagramPacket, IRelayPacketAnalyzingCallback callback) {

        if (datagramPacket == null || datagramPacket.getData().length == 0) {
            callback.emptyPacket();
            return;
        }

        byte[] relayData = new byte[datagramPacket.getLength()];
        System.arraycopy(datagramPacket.getData(), 0, relayData, 0, relayData.length);

//        RelayPacketAttributes attributes = new RelayPacketParser().parsePacket(relayData, 0);
//
//        if (attributes != null) {
//            if (attributes.getIsDevidedPacket() == AppConstants.YES) {
//                callback.receiveBrokenPacket(attributes);
//
//                RelayPacketAttributes mergedRelayPacket = mergeBrokenPackets(attributes);
//                if (mergedRelayPacket.getData() != null && mergedRelayPacket.getIsDevidedPacket() == AppConstants.NO) {
//                    mergedRelayPacket.setSocketType(SOCKET_TYPE.RELAY);
//                    callback.receiveFullPacket(mergedRelayPacket);
//                }
//
//            } else {
//                attributes.setSocketType(SOCKET_TYPE.RELAY);
//                callback.receiveFullPacket(attributes);
//            }
//        } else {
//            logger.error("Error in parsing received relay packet");
//        }
    }

    /**
     * *
     * Merge packet if it is broken
     *
     * @param attributes
     * @return
     */
//    private RelayPacketAttributes mergeBrokenPackets(RelayPacketAttributes attributes) {
//
//        byte[] brkn_data = attributes.getData();
//        long user_id = attributes.getUserID();
//        BrokenPacketAttributes brokenPacketAttributes = new BrokenPacketParser().parseAsClient(brkn_data, 0);
//
//        if (brokenPacketAttributes != null) {
//            int total_packets = brokenPacketAttributes.getTotalPacket();
//            int packet_number = brokenPacketAttributes.getPacketNumber();
//            String key = user_id + "" + brokenPacketAttributes.getServerUniqueKey();
//
//            byte[] data = brokenPacketAttributes.getData();
//            if (data != null) {
//                    brknRepository.put(key, packet_number, data);
//               
//            } else {
//                logger.error("data is null for " + attributes.getAction());
//            }
//
//            ConcurrentHashMap<Integer, byte[]> packet_list = brknRepository.get(key);
//            if (packet_list.size() == total_packets) {
//                int total_read = 0;
//                for (byte[] bytes : packet_list.values()) {
//                    total_read += bytes.length;
//                }
//
//                byte[] all_data = new byte[total_read];
//                total_read = 0;
//                for (int i = 0; i < packet_list.size(); i++) {
//                    try {
//                        byte[] broken_data = packet_list.get(i);
//                        System.arraycopy(broken_data, 0, all_data, total_read, broken_data.length);
//                        total_read += broken_data.length;
//                    } catch (Exception ex) {
//                        logger.error(ex.getLocalizedMessage()+".. Error in merging broken relay packet", ex);
//                    }
//                }
//                attributes.setData(all_data);
//                attributes.setIsDevidedPacket(AppConstants.NO);
//                brknRepository.remove(key);
//            }
//        } else {
//            logger.error("Can't parsing received broken relay packet for action " + attributes.getAction());
//        }
//        return attributes;
//    }

//    public static void main(String[] args) {
//        byte[] relayData = {1, 2, 0, 14, 27, 2, 7, -42, 2, 8, 1, 0, 1, 0, 0, 0, 0, 0, 7, 6, 0, 0, 0, 0, 0, 52, 127, 0, -89, 123, 34, 115, 117, 99, 115, 34, 58, 116, 114, 117, 101, 44, 34, 115, 116, 114, 101, 97, 109, 73, 100, 34, 58, 34, 57, 51, 56, 54, 53, 57, 54, 53, 53, 48, 48, 53, 49, 49, 49, 115, 101, 99, 114, 101, 116, 95, 99, 104, 97, 116, 95, 115, 100, 107, 49, 95, 116, 101, 115, 116, 34, 44, 34, 105, 115, 70, 110, 100, 34, 58, 102, 97, 108, 115, 101, 44, 34, 116, 109, 34, 58, 49, 52, 56, 56, 50, 54, 53, 56, 52, 55, 51, 51, 50, 44, 34, 112, 114, 73, 109, 34, 58, 34, 34, 44, 34, 102, 110, 34, 58, 34, 116, 117, 104, 105, 110, 32, 34, 44, 34, 117, 73, 100, 34, 58, 34, 116, 117, 104, 105, 110, 32, 34, 44, 34, 117, 116, 73, 100, 34, 58, 53, 50, 44, 34, 102, 111, 108, 108, 111, 119, 34, 58, 102, 97, 108, 115, 101, 44, 34, 114, 99, 34, 58, 48, 125};
//        RelayPacketAttributes attributes = new RelayPacketParser().parsePacket(relayData, 0);
//        System.out.println(attributes.toString());
//    }
    
}
