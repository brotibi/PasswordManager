package passwordmanager;

import passwordmanager.view.ExampleView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Main program entry class for creating a Model, View, and Controller, and running
 * the password manager to completion.
 */
public class PasswordManager {
    public static void main(String[] args) {
        ExampleView view = new ExampleView();
        view.run();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Error, could not get SHA-256 algorithm");
        }
    }
}
