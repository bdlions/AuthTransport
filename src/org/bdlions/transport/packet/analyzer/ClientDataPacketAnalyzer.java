/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.packet.analyzer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.DatagramPacket;
import java.util.concurrent.ConcurrentHashMap;
import org.bdlions.transport.packet.IPacketHeader;
import org.bdlions.transport.packet.PacketHeaderImpl;
import org.bdlions.transport.packet.PacketImpl;
import org.bdlions.transport.repository.HierarchicalRepository;
//import org.ipvision.byteparser.BrokenPacketParser;
//import org.ipvision.parsers.BrokenPacketAttributes;
//import org.ipvision.transport.repository.HierarchicalRepository;
//import org.ringid.utilities.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author alamgir ClientDatagramPacketMerger for merging broken packet if found
 */
public class ClientDataPacketAnalyzer {

    private final HierarchicalRepository<String, Integer, byte[]> brknRepository;
    private static final int BROKEN_PCK_TIMEOUT = 40000;
    private static final short PACKET_HEADER_LENGTH_BYTE_SIZE = 2;
    private static final short PACKET_DATA_LENGTH_BYTE_SIZE = 2;
    private final Logger logger = LoggerFactory.getLogger(ClientDataPacketAnalyzer.class);
    private final Gson gson = new GsonBuilder().create();

    public ClientDataPacketAnalyzer() {
        this.brknRepository = new HierarchicalRepository<>(1000, BROKEN_PCK_TIMEOUT);
    }

    public void analyzePacket(DatagramPacket datagramPacket, IDataPacketAnalyzingCallback callback) {

        if (datagramPacket == null || datagramPacket.getData().length == 0) {
            callback.emptyPacket();
            return;
        }
        byte[] data = datagramPacket.getData();
        int packetHeaderLength = ((data[0] & 0xff) << 8) | (data[1] & 0xff);
        if(packetHeaderLength <= 0){
            //exception thorw invalid header
            return;
        }
        byte[] pacektHeaderData = new byte[ packetHeaderLength ];
        System.arraycopy(data, PACKET_HEADER_LENGTH_BYTE_SIZE, pacektHeaderData, 0, packetHeaderLength);
        String packetHeaderContent = new String(pacektHeaderData);
        logger.debug(packetHeaderContent);
        IPacketHeader packetHeader = gson.fromJson(packetHeaderContent, PacketHeaderImpl.class);

        if (packetHeader.getRequestType() == null) {
            callback.incorrectSocketType();
        } /**
         * Receiving full packet and send to the originator
         */
        else if (!packetHeader.isBroken()) {
            int dataLenghtPosition = PACKET_HEADER_LENGTH_BYTE_SIZE + packetHeaderLength;
            String packetDataContent = null;
            PacketImpl packetImpl = new PacketImpl();
            packetImpl.setPacketHeader(packetHeader);
            packetImpl.setPacketHeaderData(pacektHeaderData);
            int packetDataLength = ((data[dataLenghtPosition] & 0xff) << 8) | (data[dataLenghtPosition + 1] & 0xff);
            if(packetDataLength > 0){
                byte[] packetDataBytes = new byte[ packetDataLength ];
                int packetDataStart = PACKET_HEADER_LENGTH_BYTE_SIZE + packetHeaderLength + PACKET_DATA_LENGTH_BYTE_SIZE;
                System.arraycopy(data, packetDataStart, packetDataBytes, 0, packetDataLength);
                packetDataContent = new String(packetDataBytes);
                packetImpl.setPacketBody(packetDataContent);
            }
            callback.receiveFullPacket(packetImpl);
        } else {
            int length = datagramPacket.getLength() - 2;
            byte[] received_data = new byte[length];
//            byte[] merged_data = null;
            if (length > 0) {
                int socket = data[0];
                System.arraycopy(data, 2, received_data, 0, length);
//                BrokenPacketAttributes brokenPacketAttributes = new BrokenPacketParser().parseAsServer(received_data, 0);
//
//                if (brokenPacketAttributes != null) {
//                    callback.receiveBrokenPacket(datagramPacket.getAddress(), datagramPacket.getPort(), brokenPacketAttributes);
//                    //merging packet logic here
//                    //send the merging packet as full packet
//                    byte[] merged_data = mergeBrokenPackets(brokenPacketAttributes, socket);
//                    if (merged_data != null) {
//                        datagramPacket.setData(merged_data);
//                        datagramPacket.setLength(merged_data.length);
//                        callback.receiveFullPacket(datagramPacket);
//                    }
//                } else {
//                    logger.error("Can't parse broken packet");
//                }
            }
        }
    }

//    private byte[] mergeBrokenPackets(BrokenPacketAttributes brknPckAttr, int socket) {
//        int total_packets = brknPckAttr.getTotalPacket();
//        int packet_number = brknPckAttr.getPacketNumber();
//        String key = brknPckAttr.getClientUniqueKey();
//
//        byte data_bytes[] = brknPckAttr.getData();
//        brknRepository.put(key, packet_number, data_bytes);
//        
//        ConcurrentHashMap<Integer, byte[]> packet_list = brknRepository.get(key);
//        if (total_packets == packet_list.size()) {
//            int total_read = 0;
//            for (byte[] bytes : packet_list.values()) {
//                total_read += bytes.length;
//            }
//            /**
//             * add 2 bytes for maintaining same format
//             */
//            byte[] all_data = new byte[total_read + 2];
//            all_data[0] = (byte) socket;
//
//            total_read = 2;
//            for (int i = 0; i < packet_list.size(); i++) {
//                try {
//                    byte[] broken_data = packet_list.get(i);
//                    System.arraycopy(broken_data, 0, all_data, total_read, broken_data.length);
//                    total_read += broken_data.length;
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    logger.error(ex.getLocalizedMessage()+"... Error in merging broken data. ",ex);
//                }
//            }
//            brknRepository.remove(key);
//            return all_data;
//        }
//        return null;
//    }
}
