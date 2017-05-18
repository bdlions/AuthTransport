/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.channel;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.bdlions.transport.channel.exceptions.ChannelOpenedException;

/**
 *
 * @author alamgir
 */
public interface IChannel {
    public void start() throws ChannelOpenedException;
    public void stop();
    public InetAddress getIP();
    public int getPort();
    public void send(byte[] data, InetAddress clientIP, int clientPort);
}
