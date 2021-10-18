/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.security.SecureRandom;
import java.math.BigInteger;
/**
 *This class is use to generate Encryption key for Blowfish or AES algorithm
 * @author Balogun Taiwo
 */
public final class EncryptionKeyGenrerator {
    private SecureRandom random = new SecureRandom();
    
    public String nextEncryptionKey(String algo){
        String encryptKey = "";
        if(algo.equalsIgnoreCase("AES")){
         //generating 128-bit(approximately 130 bit) key in a string of 32-bit for each alphanumeric character   
         encryptKey = new BigInteger(130, random).toString(32); 
        }
        else{
         //generating 56-bit(approximately 60 bit) key in a string of 32-bit for each alphanumeric character    
         encryptKey = new BigInteger(60, random).toString(32); 
        }            
        return encryptKey;
    }
}
