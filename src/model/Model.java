package model;

import lib.Utils;
import lib.CryptoIfc;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Represents the model of our password manager app, storing the internal state of the app.
 */
public class Model {
    // salt used to derive a 16-byte key from the master password
    private byte[] salt;

    // list of pairs of tags and passwords
    private ArrayList<TagPasswordPair> databaseInfo;

    public Model() {
        this.salt = null;
        this.databaseInfo = new ArrayList<>();
    }

    public void initEmpty() {
        this.databaseInfo.clear();
        this.salt = CryptoIfc.randomBytes(16);
    }

    /**
     * Reads the database at the given filename using the given password and stores the contents in databaseInof
     * @param filename the file to read (should be a *.pwdb file)
     * @param password the password used to decrypt the file
     */
    public void read(String filename, char[] password) throws ParseException {
        FileManager mngr = new FileManager(filename);
        mngr.read();

        if (!CryptoIfc.verifyPHCString(password, mngr.masterhash)) {
            throw new ParseException("Incorrect password", 0);
        }

        this.databaseInfo.clear();
        this.salt = mngr.salt;
        for (TagPasswordPair pair : mngr.db) {
            // allow tag to be decrypted since that's not the sensitive part
            String decryptedTag = String.valueOf(Utils.readableDecrypt(pair.tag, password, this.salt));
            databaseInfo.add(new TagPasswordPair(decryptedTag, pair.passwordCipher));
        }
    }

    /**
     * Dumps the contents of databaseInfo to the given file, encrypting the contents using the given password
     * @param filename the file to write to (should be a *.pwdb file)
     * @param password the password used to encrypt the file
     */
    public void write(String filename, char[] password) {
        FileManager mngr = new FileManager(filename);
        mngr.masterhash = CryptoIfc.plaintextToPHCString(password);
        mngr.salt = this.salt;
        mngr.db.clear();

        for (TagPasswordPair pair : this.databaseInfo) {
            String encryptedTag = Utils.readableEncrypt(pair.tag.toCharArray(), password, this.salt);
            mngr.db.add(new TagPasswordPair(encryptedTag, pair.passwordCipher));
        }

        mngr.write();
    }

    /** Sets the database info to the given value */
    public void setDatabaseInfo(ArrayList<TagPasswordPair> newInfo) {
        this.databaseInfo = newInfo;
    }

    /** Returns a copy of the database info */
    public ArrayList<TagPasswordPair> getDatabaseInfo() {
        // return copy so that the model isn't messed with until the View explicitly sets it
        return new ArrayList<>(this.databaseInfo);
    }

    /** Returns a copy of the salt */
    public byte[] getSalt() {
        return Arrays.copyOf(this.salt, this.salt.length);
    }
}
