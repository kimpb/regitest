package com.example.ayu.regitest;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;

public class RSA1 {
    private static Key publicKey;
    private static Key privateKey;

    public RSA1() throws NoSuchAlgorithmException, InvalidKeySpecException{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        KeyPair keyPair = keyPairGenerator.genKeyPair();
        this.publicKey = keyPair.getPublic(); // 공개키
        this.privateKey = keyPair.getPrivate(); // 개인키
    }

    public static String encrypt(String inputStr) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException{
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = c.doFinal(inputStr.getBytes("UTF-8")); // 암호화된 데이터(byte 배열)
        return new String(Base64.encodeBase64(encrypted));
    }

    public static String decrypt(String inputStr) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decrypted = Base64.decodeBase64(inputStr.getBytes());
        return new String(c.doFinal(decrypted),"UTF-8");
    }

    public static Key getPublicKey() {
        return publicKey;
    }

    public static Key getPrivateKey() {
        return privateKey;
    }
}
