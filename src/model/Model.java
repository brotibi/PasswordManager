package main.java.passwordmanager.model;

import main.java.passwordmanager.Utils;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Represents the model of our password manager app, storing the internal state of the app.
 */
public class Model {
    private ArrayList<TagPasswordPair> databaseInfo;

    public Model() {
        this.databaseInfo = new ArrayList<>();
    }

    /**
     * Reads the database at the given filename using the given password and stores the contents in databaseInof
     * @param filename the file to read (should be a *.pwdb file)
     * @param password the password used to decrypt the file
     */
    public void read(String filename, char[] password) throws ParseException {
        // TODO open and parse file
        this.databaseInfo.clear();
        databaseInfo.add(new TagPasswordPair("gmail", Utils.readableEncrypt("password for gmail".toCharArray(), password)));
        databaseInfo.add(new TagPasswordPair("paypal", Utils.readableEncrypt("p@5$w0rD_4_P4Yp41".toCharArray(), password)));
    }

    /**
     * Dumps the contents of databaseInfo to the given file, encrypting the contents using the given password
     * @param filename the file to write to (should be a *.pwdb file)
     * @param password the password used to encrypt the file
     */
    public void write(String filename, char[] password) {
        // TODO
    }

    public void setDatabaseInfo(ArrayList<TagPasswordPair> newInfo) {
        this.databaseInfo = newInfo;
    }

    public ArrayList<TagPasswordPair> getDatabaseInfo() {
        // return copy so that the model isn't messed with until the View explicitly sets it
        return new ArrayList<>(this.databaseInfo);
    }
}
