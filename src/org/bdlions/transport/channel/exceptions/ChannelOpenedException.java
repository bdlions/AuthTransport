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
public class ChannelOpenedException extends Exception{
    
    public ChannelOpenedException(String message) {
        super(message);
    }

    public ChannelOpenedException() {
        this("Channel port is in already used or cannot be opened");
    }
}
