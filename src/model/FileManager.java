package model;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import lib.CryptoIfc;

import java.util.HashMap;

import java.util.Scanner;

public class FileManager {
    private String fileName;
    private String masterKey;

    public FileManager(String fileName, String masterKey) {
        this.fileName = fileName;
        this.masterKey = masterKey;
    }

    public void createFile() {
        try {
            File myFile = new File(fileName);

            if (myFile.createNewFile()) {
                System.out.println("File created: " + myFile.getName());
            } else {
                System.out.println("File already exists");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void addPassword(String website, String hash) {
        try {
            // String str = "World";
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));

            writer.append(website);
            writer.append(":");
            writer.append(hash);
            writer.append("\n");

            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public HashMap<String, String> getPasswords() {
        try {
            File myFile = new File(fileName);
            Scanner reader = new Scanner(myFile);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                System.out.println(data);
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return null;
    }
}
