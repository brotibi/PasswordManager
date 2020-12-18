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
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(),
                byteBuffer.position(), byteBuffer.limit());
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

    public static String readableEncrypt(char[] plaintext, char[] key) {
        byte[] enc = CryptoIfc.encrypt(plaintext, key);
        return Base64.getEncoder().encodeToString(enc);
    }

    public static char[] readableDecrypt(String b64cipher, char[] key) {
        byte[] enc = Base64.getDecoder().decode(b64cipher);
        return CryptoIfc.decrypt(enc, key);
    }
}
