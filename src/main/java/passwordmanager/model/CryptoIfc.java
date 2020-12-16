package main.java.passwordmanager.model;

import passwordmanager.PasswordGenerator;

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
 *
 * TODO: read through functions and make sure that Strings are available for as little time as possible
 */
public class CryptoIfc {
    private Cipher cipher;

    public CryptoIfc() {
        try {
            this.cipher = Cipher.getInstance("AES");
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
    public byte[] encrypt(String plaintext, String keyStr) {
        try {
            Key aesKey = new SecretKeySpec(createhash(keyStr, keyStr.getBytes()), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
            return cipher.doFinal(plaintext.getBytes());
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
    public String decrypt(byte[] cipherText, String keyStr) {
        try {
            Key aesKey = new SecretKeySpec(createhash(keyStr, keyStr.getBytes()), "AES");
            cipher.init(Cipher.DECRYPT_MODE, aesKey);
            return new String(cipher.doFinal(cipherText));
        } catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Generates a 16-byte salt to be used in hashing
     * @return the random salt
     */
    public byte[] generatesalt() {
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
    public byte[] createhash(String plaintext, byte[] salt) {
        int ITERATIONS = 65536;
        int KEYLEN = 128;
        KeySpec spec = new PBEKeySpec(plaintext.toCharArray(), salt, ITERATIONS, KEYLEN);

        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
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
    public String plaintextToPHCString(String plaintext) {
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
     * @param phc PHC string generated plaintextToPHCString()
     * @return whether the plaintext could hash to the values in the PHC string
     */
    public boolean verifyPHCString(String plaintext, String phc) {
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
        CryptoIfc c = new CryptoIfc();

        PasswordGenerator gen = new PasswordGenerator();
        gen.length = 25;
        String plaintext = gen.generate();
        String aad = gen.generate();

        String phc = c.plaintextToPHCString(plaintext);

        System.out.println("Verifying hash matches plaintext");
        System.out.println("Password: " + plaintext);
        System.out.println("Hash: " + phc);
        System.out.println(c.verifyPHCString(plaintext, phc));

        byte[] ciphertext = c.encrypt(plaintext, aad);
        String decrypted = c.decrypt(ciphertext, aad);

        System.out.println(plaintext);
        System.out.println(decrypted);
    }
}
