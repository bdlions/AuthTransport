/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bdlions.transport.repository.exceptions;

/**
 *
 * @author saikat
 */
public class DuplicacyException extends Exception{

    public DuplicacyException(String message) {
        super(message);
    }
    
    public DuplicacyException(){
        this("Key already exists");
    }
    
}

