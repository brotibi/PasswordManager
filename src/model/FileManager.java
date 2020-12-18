package model;

import lib.CryptoIfc;
import lib.PasswordGenerator;
import lib.Utils;

import java.io.*;

import java.io.IOException;

import java.text.ParseException;
import java.util.*;

/**
 * Manager for the password database, supporting reading and writing.
 */
public class FileManager {
    private String filename;
    public String masterhash;
    public byte[] salt;
    public ArrayList<TagPasswordPair> db;

    public FileManager(String fileName) {
        this.filename = fileName;
        this.db = new ArrayList<>();
    }

    private void parseError() throws ParseException {
        throw new ParseException("Malformed file", 0);
    }

    /**
     * Reads a pwdb file and stores the information therein in the fields of this object.
     * @throws ParseException if the file is malformed or the read fails
     */
    public void read() throws ParseException {
        BufferedReader rd;
        try {
            rd = new BufferedReader(new FileReader(this.filename));
            this.masterhash = rd.readLine();
            if (masterhash == null) { parseError(); }

            String saltb64 = rd.readLine();
            if (saltb64 == null) { parseError(); }
            this.salt = Base64.getDecoder().decode(saltb64);

            String line = rd.readLine();
            while (line != null) {
                String[] spl = line.split("\\$");

                if (spl.length != 2) { parseError(); }

                String tagStr = spl[0];
                String pwStr = spl[1];
                this.db.add(new TagPasswordPair(tagStr, pwStr));

                line = rd.readLine();
            }
        } catch (IOException e) {
            throw new ParseException("Could not read file", 0);
        }
    }

    /**
     * Writes the stored data to the pwdb file. Note that before this is called, masterhash and salt must be set.
     */
    public void write() {
        try {
            File fout = new File(this.filename);
            FileOutputStream fos = new FileOutputStream(fout);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

            bw.write(masterhash);
            bw.newLine();
            bw.write(Base64.getEncoder().encodeToString(this.salt));
            bw.newLine();

            for (TagPasswordPair pair : this.db) {
                bw.write(pair.tag + "$" + pair.passwordCipher);
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PasswordGenerator gen = new PasswordGenerator();
        gen.length = 25;
        String plaintext = gen.generate();
        String key = "hello";
        byte[] salt = CryptoIfc.randomBytes(16);

        FileManager man = new FileManager("passwords.pwdb");
        man.masterhash = CryptoIfc.plaintextToPHCString(plaintext.toCharArray());
        man.salt = salt;
        String[] tags = {"gmail", "paypal"};
        String[] password = {"password", "p45$w0rd_laksdjflaksjf"};
        for (int i = 0; i < 2; i++) {
            man.db.add(new TagPasswordPair(
                    Utils.readableEncrypt(tags[i].toCharArray(), key.toCharArray(), salt),
                    Utils.readableEncrypt(password[i].toCharArray(), key.toCharArray(), salt)));
        }
        man.write();

        FileManager man2 = new FileManager("passwords.pwdb");
        try {
            man2.read();
            for (TagPasswordPair pair : man2.db) {
                System.out.println(Utils.readableDecrypt(pair.passwordCipher, key.toCharArray(), salt));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
