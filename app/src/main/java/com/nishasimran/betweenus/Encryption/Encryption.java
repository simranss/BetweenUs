package com.nishasimran.betweenus.Encryption;

import android.util.Log;

import com.nishasimran.betweenus.Utils.Utils;
import com.nishasimran.betweenus.Values.CommonValues;
import com.nishasimran.betweenus.Values.EncryptionString;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Encryption {

    private final static String TAG = "Encryption";

    public static HashMap<String, String> encryptText(@NotNull String text, String serverPublicKey, long millis) {
        byte[] bM = text.getBytes();

        Log.d(TAG, "text: " + text);
        Log.d(TAG, "textToBytes: " + Arrays.toString(bM));
        Log.d(TAG, "bytesToText: " + new String(bM));

        Random random = new Random();

        int privateKey = random.nextInt(10);
        long publicKey = generatePublicKey(privateKey, millis);

        byte[] sharedKey = generateSharedKey(privateKey, Long.parseLong(serverPublicKey));

        SecretKey encryptionKey = new SecretKeySpec(sharedKey, 0, sharedKey.length, EncryptionString.ALGORITHM_AES);

        HashMap<String, String> map = null;

        try {
            Cipher cipher = Cipher.getInstance(EncryptionString.TRANSFORMATION_AES);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            byte[] ciphertext = cipher.doFinal(bM);
            byte[] iv = cipher.getIV();
            Log.d(TAG, "cipherText: " + Arrays.toString(ciphertext));

            map = new HashMap<>();
            map.put(CommonValues.MY_PRIVATE_KEY, String.valueOf(privateKey));
            map.put(CommonValues.MY_PUBLIC_KEY, String.valueOf(publicKey));
            map.put(CommonValues.SERVER_KEY, serverPublicKey);
            map.put(CommonValues.ENCRYPTED_MESSAGE, Arrays.toString(ciphertext));
            map.put(CommonValues.IV, Arrays.toString(iv));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return map;
    }

    public static String decryptText(String serverPublicKey, String privateKeyStr, String ivStr, String encryptedText) {
        String decryptedMessage = null;
        long serverKey = Long.parseLong(serverPublicKey);
        int privateKey = Integer.parseInt(privateKeyStr);
        byte[] cipherText = Utils.stringByteArrayToByteArray(encryptedText);
        byte[] iv = Utils.stringByteArrayToByteArray(ivStr);

        byte[] sharedKey = generateSharedKey(privateKey, serverKey);

        SecretKey decryptionKey = new SecretKeySpec(sharedKey, 0, sharedKey.length, EncryptionString.ALGORITHM_AES);

        try {

            Cipher decryptCipher = Cipher.getInstance(EncryptionString.TRANSFORMATION_AES);
            decryptCipher.init(Cipher.DECRYPT_MODE, decryptionKey, new IvParameterSpec(iv));
            byte[] decryptedBytes = decryptCipher.doFinal(cipherText);
            decryptedMessage = new String(decryptedBytes);
            Log.d(TAG, "decryptedBytes: " + Arrays.toString(decryptedBytes));
            Log.d(TAG, "decryptedMessage: " + decryptedMessage);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return decryptedMessage;
    }


    public static long generatePublicKey(int a, long G) {
        return G + a;
    }


    public static byte[] generateSharedKey(int a, long p2) {
        long x = p2 + a;
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
}
