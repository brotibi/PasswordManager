package main.java.passwordmanager;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class Utils {
    public static byte[] toBytes(char[] chars) {
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
        return bytes;
    }

    public static char[] toChars(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        CharBuffer charBuffer = StandardCharsets.UTF_8.decode(byteBuffer);
        char[] chars = Arrays.copyOfRange(charBuffer.array(), charBuffer.position(), charBuffer.limit());
        Arrays.fill(charBuffer.array(), '\0');
        return chars;
    }

    /**
     * Encrypt the given plaintext with a key derived from the given key and salt and return the encoded value
     * in base64
     */
    public static String readableEncrypt(char[] plaintext, char[] key, byte[] salt) {
        byte[] realkey = CryptoIfc.createhash(key, salt);
        byte[] enc = CryptoIfc.encrypt(plaintext, realkey);
        return Base64.getEncoder().encodeToString(enc);
    }

    /**
     * Given a base64 string obtained from readableEncrypt(), decode from base64, then decrypt the resulting cipher
     * with a key derived from the given key and salt
     */
    public static char[] readableDecrypt(String b64cipher, char[] key, byte[] salt) {
        byte[] realkey = CryptoIfc.createhash(key, salt);
        byte[] enc = Base64.getDecoder().decode(b64cipher);
        return CryptoIfc.decrypt(enc, realkey);
    }
}
