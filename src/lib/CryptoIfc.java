package lib;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * Interface for encryption, decryption, hashing, and hash validation functions.
 */
public class CryptoIfc {
    private static Cipher cipher;
    private static final String encryptAlgo = "AES/CBC/PKCS5Padding";
    private static final String hashAlgo = "PBKDF2WithHmacSHA1";
    private static final int IVLEN = 16;

    static {
        try {
            cipher = Cipher.getInstance(encryptAlgo);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Encrypts the given plaintext string using the given key string. The key string is hashed before being used as
     * the actual encryption key.
     * @param plaintext plaintext string to be encrypted
     * @param key string to base the encryption key off of
     * @return the cipher text derived from encrypting the plaintext
     */
    public static byte[] encrypt(char[] plaintext, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        byte[] iv = randomBytes(IVLEN);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            byte[] ciphertext = cipher.doFinal(Utils.toBytes(plaintext));
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);
            return byteBuffer.array();
        } catch (InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }

    /**
     * Decrypts the given cipher text using the given key string. The key string is hashed before being used as the
     * actual encryption key.
     * @param cipherMessage cipher text derived from the encrypt() function
     * @param key string to base the encryption key off of
     * @return the cipher text derived from encrypting the plaintext
     */
    public static char[] decrypt(byte[] cipherMessage, byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, "AES");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(cipherMessage, 0, IVLEN);
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            return Utils.toChars(cipher.doFinal(cipherMessage, IVLEN, cipherMessage.length - IVLEN));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return new char[]{};
        }
    }

    /**
     * Generates a random nBytes-byte value
     * @param nBytes number of bytes to generate
     * @return a byte array of the random bytes
     */
    public static byte[] randomBytes(int nBytes) {
        SecureRandom random = new SecureRandom();
        byte[] out = new byte[nBytes];
        random.nextBytes(out);
        return out;
    }

    /**
     * Hashes the given plaintext string with the given salt
     * @param plaintext plaintext string to hash
     * @param salt salt to use in hashing
     * @return the hash
     */
    public static byte[] createhash(char[] plaintext, byte[] salt) {
        int ITERATIONS = 65536;
        int KEYLEN = 128;
        KeySpec spec = new PBEKeySpec(plaintext, salt, ITERATIONS, KEYLEN);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(hashAlgo);
            return factory.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }

    /**
     * Hashes the given plaintext using randomly-generated salt and returns a PHC string containing the salt and hash
     * The returned string takes the form "<base64 encoded salt>$<base64 encoded hash>"
     * @param plaintext plaintext to hash
     * @return the PHC string containing salt and the hashed plaintext
     */
    public static String plaintextToPHCString(char[] plaintext) {
        byte[] salt = randomBytes(16);
        byte[] hash = createhash(plaintext, salt);

        // Base64 encode to ease debugging and to ensure that '$' will not appear in the string
        // This is (I think) how PHC is typically done
        String saltStr = Base64.getEncoder().encodeToString(salt);
        String hashStr = Base64.getEncoder().encodeToString(hash);

        return saltStr + '$' + hashStr;
    }

    /**
     * Verifies the given plaintext string against the given PHC string, returning whether the plaintext could
     * hash to the values contained in the PHC string
     * @param plaintext plaintext data to validate
     * @param phc PHC string generated by plaintextToPHCString()
     * @return whether the plaintext could hash to the values in the PHC string
     */
    public static boolean verifyPHCString(char[] plaintext, String phc) {
        // Has format <salt>$<hash>
        String[] spl = phc.split("\\$");

        if (spl.length != 2) {
            return false;
        }

        // Extract salt and hash, then decode them from base64
        String saltStr = spl[0];
        String hashStr = spl[1];
        byte[] salt = Base64.getDecoder().decode(saltStr);
        byte[] hash = Base64.getDecoder().decode(hashStr);

        // Recompute hash and ensure that stored hash equals the computed one
        byte[] rehashed = createhash(plaintext, salt);
        return Arrays.equals(rehashed, hash);
    }

    private static void testEncryptDecrypt() {
        System.out.println("\nVerifying again but with many variable string and key sizes");
        PasswordGenerator gen = new PasswordGenerator();
        for (int i = 1; i < 128; i++) {
            System.out.println(i);
            gen.length = i;
            String plaintext = gen.generate();
            String key = gen.generate();
            byte[] realKey = CryptoIfc.createhash(key.toCharArray(), CryptoIfc.randomBytes(16));

            byte[] ciphertext = CryptoIfc.encrypt(plaintext.toCharArray(), realKey);
            char[] decrypted = CryptoIfc.decrypt(ciphertext, realKey);

            assert(plaintext.equals(String.valueOf(decrypted)));
        }
    }

    public static void main(String[] args) {
        PasswordGenerator gen = new PasswordGenerator();
        gen.length = 32;
        String plaintext = gen.generate();
        String key = gen.generate();
        byte[] realKey = CryptoIfc.createhash(key.toCharArray(), CryptoIfc.randomBytes(16));

        String phc = CryptoIfc.plaintextToPHCString(plaintext.toCharArray());

        System.out.println("Verifying hash matches plaintext");
        System.out.println("Password: " + plaintext);
        System.out.println("Hash: " + phc);
        System.out.println(CryptoIfc.verifyPHCString(plaintext.toCharArray(), phc));

        System.out.println("\nVerifying that encrypting then decrypting a string results in the same string");
        byte[] ciphertext = CryptoIfc.encrypt(plaintext.toCharArray(), realKey);
        char[] decrypted = CryptoIfc.decrypt(ciphertext, realKey);

        System.out.println("Before: " + plaintext);
        System.out.println("Encrypted: " + new String(ciphertext));
        System.out.println("After:  " + String.valueOf(decrypted));

        testEncryptDecrypt();
    }
}