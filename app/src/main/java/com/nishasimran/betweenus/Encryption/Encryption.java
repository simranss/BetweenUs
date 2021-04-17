package com.nishasimran.betweenus.Encryption;

import android.util.Log;

import com.nishasimran.betweenus.Strings.CommonStrings;
import com.nishasimran.betweenus.Strings.EncryptionString;
import com.nishasimran.betweenus.Utils.Utils;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

import org.jetbrains.annotations.NotNull;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import de.frank_durr.ecdh_curve25519.ECDHCurve25519;

public class Encryption {

    private final static String TAG = "Encryption";

    static {
        String TAG = "MainActivity";
        // Load native library ECDH-Curve25519-Mobile implementing Diffie-Hellman key
        // exchange with elliptic curve 25519.
        try {
            System.loadLibrary("ecdhcurve25519");
            Log.i(TAG, "Loaded ecdhcurve25519 library.");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "Error loading ecdhcurve25519 library: " + e.getMessage());
        }
    }




    public static HashMap<String, String> encryptText(@NotNull String text, String serverPublicKey) {
        byte[] bM = text.getBytes(StandardCharsets.ISO_8859_1);
        SecureRandom random = new SecureRandom();

        byte[] privateKey = ECDHCurve25519.generate_secret_key(random);
        byte[] publicKey = ECDHCurve25519.generate_public_key(privateKey);
        byte[] serverKey = Utils.stringByteArrayToByteArray(serverPublicKey);
        byte[] sharedKey = ECDHCurve25519.generate_shared_secret(privateKey, serverKey);

        SecretKey encryptionKey = new SecretKeySpec(sharedKey, 0, sharedKey.length, EncryptionString.ALGORITHM_AES);

        HashMap<String, String> map = null;

        try {
            Cipher cipher = Cipher.getInstance(EncryptionString.TRANSFORMATION_AES);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            byte[] ciphertext = cipher.doFinal(bM);
            byte[] iv = cipher.getIV();
            Log.d(TAG, "cipherText: " + Arrays.toString(ciphertext));

            map = new HashMap<>();
            map.put(CommonStrings.MY_PRIVATE_KEY, Arrays.toString(privateKey));
            map.put(CommonStrings.MY_PUBLIC_KEY, Arrays.toString(publicKey));
            map.put(CommonStrings.SERVER_KEY, serverPublicKey);
            map.put(CommonStrings.ENCRYPTED_MESSAGE, Arrays.toString(ciphertext));
            map.put(CommonStrings.IV, Arrays.toString(iv));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return map;
    }


    public static String decryptText(String serverPublicKey, String private_key, String ivStr, String encryptedText) {
        String decryptedMessage = null;
        byte[] serverKey = Utils.stringByteArrayToByteArray(serverPublicKey);
        byte[] privateKey = Utils.stringByteArrayToByteArray(private_key);
        byte[] cipherText = Utils.stringByteArrayToByteArray(encryptedText);
        byte[] iv = Utils.stringByteArrayToByteArray(ivStr);

        SecureRandom random = new SecureRandom();

        byte[] sharedKey = ECDHCurve25519.generate_shared_secret(privateKey, serverKey);

        SecretKey decryptionKey = new SecretKeySpec(sharedKey, 0, sharedKey.length, EncryptionString.ALGORITHM_AES);

        try {

            Cipher decryptCipher = Cipher.getInstance(EncryptionString.TRANSFORMATION_AES);
            decryptCipher.init(Cipher.DECRYPT_MODE, decryptionKey, new IvParameterSpec(iv));
            byte[] decryptedBytes = decryptCipher.doFinal(cipherText);
            decryptedMessage = new String(decryptedBytes, StandardCharsets.ISO_8859_1);
            Log.d(TAG, "decryptedBytes: " + Arrays.toString(decryptedBytes));
            Log.d(TAG, "decryptedMessage: " + decryptedMessage);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | BadPaddingException | IllegalBlockSizeException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            e.printStackTrace();
        }

        return decryptedMessage;
    }

}
