/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.channel.provider;

import java.util.ArrayList;
import org.bdlions.transport.channel.exceptions.ChannelNotFoundException;
import org.bdlions.transport.sender.IClientPacketSender;
import org.bdlions.transport.sender.IRelayPacketSender;

/**
 *
 * @author saikat
 */
public interface IChannelProvider {
    ArrayList<IClientPacketSender> getClientChannelList();
    IClientPacketSender getClientChannel(int i) throws ChannelNotFoundException;
    IRelayPacketSender getRelayChannel() throws ChannelNotFoundException;
}
