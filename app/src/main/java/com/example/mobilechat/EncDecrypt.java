package com.example.mobilechat;

import android.util.Log;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Encrypt and Decrypt Messages
 **/


public class EncDecrypt {

    String key = "abcdefghijklmnop";

    public String encrypt(String unencryptedText) {

        String encryptedText = "";

        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            byte[] encrypted = cipher.doFinal(unencryptedText.getBytes());

            //Convert from byte array to hex StringBuilder
            StringBuilder sb = new StringBuilder();
            for (byte b: encrypted) {
                sb.append(String.format("%02X", b));
            }

            encryptedText = sb.toString();
            Log.d("myMsg", "Encrypted text: " + encryptedText);


        } catch (NoSuchAlgorithmException e) {
            // Cipher.getInstance
            Log.d("myMsg", "In exception decrypt..." + e);
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // Cipher.getInstance
            Log.d("myMsg", "In exception decrypt..." + e);
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // Cipher.init
            Log.d("myMsg", "In exception decrypt..." + e);
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // cipher.doFinal
            Log.d("myMsg", "In exception decrypt..." + e);
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // cipher.doFinal
            Log.d("myMsg", "In exception decrypt..." + e);
            e.printStackTrace();
        }

        return encryptedText;
    } // end method encrypt

    public String decrypt(String encryptedText) {

        String decryptedText = "";

        byte[] encrypted = hexStringToByteArray(encryptedText);

        try {
            Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            decryptedText = new String(cipher.doFinal(encrypted));
            Log.d("myRecMsg", "Decrypted text: " + decryptedText);

        } catch (NoSuchAlgorithmException e) {
            // Cipher.getInstance
            Log.d("myMsg", "In exception EncDecrypt..." + e);
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // Cipher.getInstance
            Log.d("myMsg", "In exception EncDecrypt..." + e);
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // Cipher.init
            Log.d("myMsg", "In exception EncDecrypt..." + e);
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // cipher.doFinal
            Log.d("myMsg", "In exception EncDecrypt..." + e);
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // cipher.doFinal
            Log.d("myMsg", "In exception EncDecrypt..." + e);
            e.printStackTrace();
        }

        return decryptedText;
    } // end decrypt method

    // helper function to convert hex string to a byte array
    // Every two hex digits becomes one byte
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i+=2) {
            data [i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
            + Character.digit(s.charAt(i + 1), 16));
        }
        return data;

    }

}