package main.java.passwordmanager;

import javax.crypto.*;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

/**
 * Interface for encryption, decryption, hashing, and hash validation functions.
 */
public class CryptoIfc {
    private static Cipher cipher;
    private static final String encryptAlgo = "AES";
    private static final String hashAlgo = "PBKDF2WithHmacSHA1";

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
     * @param keyStr string to base the encryption key off of
     * @return the cipher text derived from encrypting the plaintext
     */
    public static byte[] encrypt(char[] plaintext, char[] keyStr) {
        return encrypt(plaintext, keyStr, Utils.toBytes(keyStr));
    }

    public static byte[] encrypt(char[] plaintext, char[] keyStr, byte[] salt) {
        try {
            // Note that here, the key is used as its own salt in the hash function. When hashing passwords, this is a
            // terrible idea because it defeats the purpose of salt. Here, however, it's fine because this key is not
            // stored in the file, so the attacker could only ever access it through a memory dump
            Key aesKey = new SecretKeySpec(createhash(keyStr, salt), encryptAlgo);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return cipher.doFinal(Utils.toBytes(plaintext));
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return new byte[]{};
        }
    }

    /**
     * Decrypts the given cipher text using the given key string. The key string is hashed before being used as the
     * actual encryption key.
     * @param cipherText cipher text derived from the encrypt() function
     * @param keyStr string to base the encryption key off of
     * @return the cipher text derived from encrypting the plaintext
     */
    public static char[] decrypt(byte[] cipherText, char[] keyStr) {
        return decrypt(cipherText, keyStr, Utils.toBytes(keyStr));
    }

    public static char[] decrypt(byte[] cipherText, char[] keyStr, byte[] salt) {
        try {
            Key aesKey = new SecretKeySpec(createhash(keyStr, salt), encryptAlgo);
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return Utils.toChars(cipher.doFinal(cipherText));
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return new char[]{};
        }
    }

    /**
     * Generates a 16-byte salt to be used in hashing
     * @return the random salt
     */
    public static byte[] generatesalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
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
        byte[] salt = generatesalt();
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

    public static void main(String[] args) {
        PasswordGenerator gen = new PasswordGenerator();
        gen.length = 25;
        String plaintext = gen.generate();
        String key = gen.generate();

        String phc = CryptoIfc.plaintextToPHCString(plaintext.toCharArray());

        System.out.println("Verifying hash matches plaintext");
        System.out.println("Password: " + plaintext);
        System.out.println("Hash: " + phc);
        System.out.println(CryptoIfc.verifyPHCString(plaintext.toCharArray(), phc));

        System.out.println("\nVerifying that encrypting then decrypting a string results in the same string");
        byte[] ciphertext = CryptoIfc.encrypt(plaintext.toCharArray(), key.toCharArray());
        char[] decrypted = CryptoIfc.decrypt(ciphertext, key.toCharArray());

        System.out.println("Before: " + plaintext);
        System.out.println("Encrypted: " + new String(ciphertext));
        System.out.println("After:  " + String.valueOf(decrypted));
    }
}
