/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.channel.exceptions;

/**
 *
 * @author alamgir
 */
public class ChannelNotFoundException extends Exception{

    public ChannelNotFoundException(String message) {
        super(message);
    }

    public ChannelNotFoundException() {
        this("Channel not found or channel is not initialized");
    }
    
    
}
