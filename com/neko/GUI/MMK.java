package com.neko.GUI;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import javax.crypto.*;
import javaxvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.EnumSet;
import java.util.function.Predicate;

public class MMK {
    private static final Gson gson = new Gson();

    public static boolean isJson(byte[] data) {
        return isValid(data, json -> {
            try {
                gson.fromJson(new String(json, StandardCharsets.UTF_8), JsonElement.class);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public static boolean isImage(byte[] data) {
        return isValid(data, image -> {
            try {
                return ImageIO.read(new ByteArrayInputStream(image)).getWidth() != 0;
            } catch (Exception e) {
                return false;
            }
        });
    }

    public static boolean isMp4(byte[] data) {
        return data != null && data.length > 7 && data[4] == 0x66 && data[5] == 0x74 && data[6] == 0x79 && data[7] == 0x70;
    }

    private static boolean isValid(byte[] data, Predicate<byte[]> validator) {
        return validator.test(data);
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
                this.cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.key, transformation.split("/")[0]), new IvParameterSpec(new byte[16]));
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }

        public static SecretKey getFromJson(byte[] data) {
            return getSecretKey(data, MMK::isJson);
        }

        public static SecretKey getFromImage(byte[] data) {
            return getSecretKey(data, MMK::isImage);
        }

        public static SecretKey getFromMp4(byte[] data) {
            return getSecretKey(data, MMK::isMp4);
        }

        private static SecretKey getSecretKey(byte[] data, Predicate<byte[]> validator) {
            return EnumSet.allOf(SecretKey.class).stream()
                    .filter(secretKey -> validator.test(secretKey.aes(data)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("没有符合的SecretKey"));
        }

        public byte[] aes(byte[] data) {
            try {
                return cipher.doFinal(data);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                return null;
            }
        }
    }
}