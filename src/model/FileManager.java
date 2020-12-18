package passwordmanager.model;

import passwordmanager.PasswordGenerator;
import passwordmanager.Utils;

import java.io.*;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;
// /import java.io.Exception;

import javax.crypto.*;
import java.security.*;

import passwordmanager.model.CryptoIfc;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Scanner;

public class FileManager {
    private String fileName;
    private char[] masterKey;
    private static Cipher cipher;
    private static Key aesKey;
    private static final String encryptAlgo = "AES";
    private static final String hashAlgo = "PBKDF2WithHmacSHA1";
    //CryptoIfc crypt = new CryptoIfc();
    //Utils utils = new Utils();
    

    static {
        try {
            cipher = Cipher.getInstance(encryptAlgo);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public FileManager(String fileName, char[] masterKey) {
        this.fileName = fileName;
        this.masterKey = masterKey;
        Key aesKey = new SecretKeySpec(CryptoIfc.createhash(masterKey, Utils.toBytes(masterKey)), encryptAlgo);
        this.aesKey = aesKey;
        System.out.println("my key " + new String(aesKey.getEncoded()));

    }

    public void createFile() {
        try {
            File myFile = new File(fileName);

            if (myFile.createNewFile()) {
                try {
                    cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                }
                HashMap<String,byte[]> h = new HashMap<String,byte[]>();
                FileOutputStream fileOut = new FileOutputStream(fileName);
                CipherOutputStream cOut = new CipherOutputStream(fileOut, cipher);
                ObjectOutputStream out = new ObjectOutputStream(cOut);
    
                out.writeObject(h);
                out.close();
                cOut.close();
                fileOut.close();
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void addPassword(String name, byte[] hash, char[] masterKey) {
        HashMap<String,byte[]> h = null;
        try {
            try {
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
            }
            FileInputStream fileIn = new FileInputStream(fileName);
            CipherInputStream cIn = new CipherInputStream(fileIn, cipher);
            ObjectInputStream in = new ObjectInputStream(cIn);
            h = (HashMap<String,byte[]>)in.readObject();
            in.close();
            cIn.close();
            fileIn.close();
            /*CryptoIfc crypt = new CryptoIfc();

            // String str = "World";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
            String text = website + " " + hash;
            System.out.println(text);
            byte[] textEncrypted = crypt.encrypt(text.toCharArray(), masterKey);
            String stringEncrypted = new String(Utils.toChars(textEncrypted));
            System.out.println("lengths" + textEncrypted.length + " " + stringEncrypted.length());
            System.out.println("testing: " + stringEncrypted);
            String decData = new String(crypt.decrypt(stringEncrypted.getBytes(), masterKey));
            System.out.println("testing");
            //System.out.println(decData);
            writer.append(stringEncrypted);
            writer.append("\n");

            writer.close();*/
            
            String text = name + ":" + hash;
            //byte[] textEncrypted = CryptoIfc.encrypt(text.toCharArray(), masterKey);
            h.put(name, hash);

            try {
                cipher.init(Cipher.ENCRYPT_MODE, aesKey);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
            }

            FileOutputStream fileOut = new FileOutputStream(fileName);
            CipherOutputStream cOut = new CipherOutputStream(fileOut, cipher);
            ObjectOutputStream out = new ObjectOutputStream(cOut);

            out.writeObject(h);
            out.close();
            cOut.close();
            fileOut.close();
        } catch (IOException | ClassNotFoundException  e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public HashMap<String, byte[]> getPasswords(char[] masterKey) {
        HashMap map = new HashMap<String, byte[]>();
        
        HashMap<String,byte[]> h = null;
        try {
            try {
                cipher.init(Cipher.DECRYPT_MODE, aesKey);
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
            }
            FileInputStream fileIn = new FileInputStream(fileName);
            CipherInputStream cIn = new CipherInputStream(fileIn, cipher);
            ObjectInputStream in = new ObjectInputStream(cIn);
            
            h = (HashMap<String,byte[]>)in.readObject();

            
            /*
            File myFile = new File(fileName);
            Scanner reader = new Scanner(myFile);
            while (reader.hasNextLine()) {
                // try {

                String data = reader.nextLine().replace("\n", "");
                if (data.length() > 1) {
                    System.out.println(data);
                    String decData = new String(crypt.decrypt(Utils.toBytes(data.toCharArray()), masterKey));
                    String[] entry = decData.split(" ", 1);
                    map.put(entry[0], entry[1]);

                    System.out.println(data);
                    System.out.println(decData);
                    // } catch(UnsupportedEncodingException ex){
                    // System.out.println("Unsupported character set"+ex);
                    // }
                }
            }*/

            in.close();
            cIn.close();
            fileIn.close();

            return h;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
            return map;
        }
    }

    public static void main(String[] args) {
        
        PasswordGenerator gen = new PasswordGenerator();
        gen.length = 25;
        String plaintext = gen.generate();
        String key = "hello";//gen.generate();


        System.out.println("\nVerifying that encrypting then decrypting a string results in the same string");
        byte[] ciphertext = CryptoIfc.encrypt(plaintext.toCharArray(), key.toCharArray());
        char[] decrypted = CryptoIfc.decrypt(ciphertext, key.toCharArray());

        System.out.println("Before: " + plaintext);
        System.out.println("Encrypted: " + new String(ciphertext));
        System.out.println("After:  " + String.valueOf(decrypted));

        FileManager man = new FileManager("passwords.txt", key.toCharArray());
        man.createFile();
        System.out.println(decrypted);
        man.addPassword("site", ciphertext, key.toCharArray());
        System.out.println("Passwords: " + man.getPasswords(key.toCharArray()));
        
    }
}
