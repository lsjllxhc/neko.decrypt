package com.neko.decrypt;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class MMK {
    private static final Gson gson = new Gson();

    public static boolean isJson(byte[] json) {
        try {
            gson.fromJson(new String(json, StandardCharsets.UTF_8), JsonElement.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isImage(byte[] image) {
        try {
            return ImageIO.read(new ByteArrayInputStream(image)).getWidth() != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMp4(byte[] mp4) {
        if (mp4 == null) return false;
        return  mp4[4] == 0x66 & mp4[5] == 0x74 & mp4[6] == 0x79 & mp4[7] == 0x70;
    }

    public enum SecretKey {
        PE("mimikkouiaeskey2", "AES/CTR/NoPadding"),
        PC("mimikkopcaeskey2", "AES/CTR/NoPadding"),
        OLD_PE("mimikkouiaeskey2", "AES/CBC/PKCS5Padding");
        private final byte[] key;
        private final Cipher cipher;

        SecretKey(String key, String transformation) {
            this.key = key.getBytes(StandardCharsets.UTF_8);
            try {
                this.cipher = Cipher.getInstance(transformation);

                this.cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.key,  this.cipher.getAlgorithm().split("/",  2)[0]), new IvParameterSpec(new byte[16]));
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                     InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }

        public static SecretKey getFromJson(byte[] json) {
            for (SecretKey secretKey : SecretKey.values()) {
                if (isJson(secretKey.aes(json)))
                    return secretKey;
            }
            throw new RuntimeException("没有符合的SecretKey");
        }

        public static SecretKey getFromImage(byte[] image) {
            for (SecretKey secretKey : SecretKey.values()) {
                if (isImage(secretKey.aes(image)))
                    return secretKey;
            }
            throw new RuntimeException("没有符合的SecretKey");
        }

        public static SecretKey getFromMp4(byte[] mp4) {
            for (SecretKey secretKey : SecretKey.values()) {
                if (isMp4(secretKey.aes(mp4)))
                    return secretKey;
            }
            throw new RuntimeException("没有符合的SecretKey");
        }

        public byte[] aes(byte[] data) {
            try {
                return cipher.doFinal(data, 0, data.length);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                return null;
            }
        }
    }

}