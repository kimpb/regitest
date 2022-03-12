
package com.example.ayu.regitest;

import java.math.BigInteger;
import java.security.MessageDigest;

public class SHA516_Hash_InCode {

    public String SHA516_Hash_InCode(String pwd) {
        try{
            String hexSapassword = pwd;
            String mixPassword = hexSapassword;
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            messageDigest.reset();
            messageDigest.update(mixPassword.getBytes("utf8"));
            String enPassword = String.format("%0128x", new BigInteger(1, messageDigest.digest()));
            //출력
            return enPassword;
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}