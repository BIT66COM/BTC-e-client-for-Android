package com.QuarkLabs.BTCeClient;

import android.content.Context;
import android.provider.Settings;
import android.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class SecurityManager {

    private static SecurityManager sInstance;
    private SecretKey mKey;

    private SecurityManager() {
    }

    private SecurityManager(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            DESKeySpec keySpec = new DESKeySpec(androidId.getBytes("UTF8"));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            mKey = keyFactory.generateSecret(keySpec);
        } catch (InvalidKeyException | UnsupportedEncodingException
                | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }

    public static SecurityManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SecurityManager(context);
        }
        return sInstance;
    }

    public String encryptString(String stringToEncrypt) {
        String output = stringToEncrypt;
        try {
            byte[] clearText = stringToEncrypt.getBytes("UTF8");
            Cipher cipher = Cipher.getInstance("DES"); // cipher is not thread safe
            cipher.init(Cipher.ENCRYPT_MODE, mKey);
            output = new String(Base64.encode(cipher.doFinal(clearText), Base64.DEFAULT), "UTF8");
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
                | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return output;
    }

    public String decryptString(String stringToDecrypt) {
        String output = stringToDecrypt;
        try {
            byte[] encrypedPwdBytes = Base64.decode(stringToDecrypt, Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("DES");// cipher is not thread safe
            cipher.init(Cipher.DECRYPT_MODE, mKey);
            output = new String(cipher.doFinal(encrypedPwdBytes), "UTF8");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        return output;
    }
}
