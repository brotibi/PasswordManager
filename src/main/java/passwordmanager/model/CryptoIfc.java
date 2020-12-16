package passwordmanager.model;

import com.google.crypto.tink.Aead;
import com.google.crypto.tink.KeysetHandle;
import com.google.crypto.tink.aead.AesGcmKeyManager;
import com.google.crypto.tink.config.TinkConfig;
import passwordmanager.PasswordGenerator;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;


/**
 * https://github.com/google/tink/blob/master/docs/JAVA-HOWTO.md shows how to set Tink up
 * You need to allow Intellij to build using Maven, though, which I learned how to do here:
 * https://www.jetbrains.com/help/idea/convert-a-regular-project-into-a-maven-project.html#add_maven_support
 */

public class CryptoIfc {

    private Aead aead;

    public CryptoIfc() {
        try {
            TinkConfig.register();
            KeysetHandle keysetHandle = KeysetHandle.generateNew(AesGcmKeyManager.aes128GcmTemplate());
            this.aead = keysetHandle.getPrimitive(Aead.class);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public byte[] encrypt(String plaintext, String salt) {
        try {
            return aead.encrypt(plaintext.getBytes(), salt.getBytes());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return new byte[]{};
    }

    public String decrypt(byte[] ciphertext, String salt) {
        try {
            return new String(aead.decrypt(ciphertext, salt.getBytes()));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        CryptoIfc c = new CryptoIfc();

        PasswordGenerator gen = new PasswordGenerator();
        gen.length = 25;
        String plaintext = gen.generate();
        String aad = gen.generate();

        byte[] ciphertext = c.encrypt(plaintext, aad);
        String decrypted = c.decrypt(ciphertext, aad);

        System.out.println(plaintext);
        System.out.println(decrypted);
    }
}
