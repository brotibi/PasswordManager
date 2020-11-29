//Author: Justin Raynor
//Public class that creates a user defined number of passwords of user defined strength,
//checks if the password is unique and returns the password to the user.
//TODO: create GUI for user to define values for the specified fields below.

import java.util.Arrays;

public class PasswordGenerator {

    public static void main(String[] args) {

        //number of passwords to be generated
        int num_passwords = 10;

        //length of password
        int passwords_length = 15;

        //specify characters to use
        boolean contains_digits = true;
        boolean contains_upper_case_letters = true;
        boolean contains_lower_case_letters = true;
        boolean contains_special_characters = true;
        boolean contains_space = true;

        //password rules (user can set each depending on the password requirement)
        boolean can_start_with_number = false;
        boolean can_start_with_special_char = false;
        boolean can_start_with_space = false;
        boolean contains_at_least_two_letters = true;
        boolean contains_at_least_two_num = true;
        boolean contains_at_least_two_special_char = true;

        //generate and store random passwords that meet all requirements above
        String[] random_passwords = new String[num_passwords];

        for(int i=0; i<num_passwords; i++) {

            String password = "";
            
            for(int j=0; j<passwords_length; j++) {
                password += generateRandomChar(contains_digits, contains_upper_case_letters, contains_lower_case_letters, contains_special_characters, contains_space);
            }

            //check if password meets rules

            //replace first character if it can't start with a space
            if(!can_start_with_space) {

                while(password.startsWith(" ")) {
                    char new_first_char = generateRandomChar(contains_digits, contains_upper_case_letters, contains_lower_case_letters, contains_special_characters, false);
                    password = password.substring(0, 0) +  new_first_char + password.substring(0 + 1);
                }

            }

            //replace first character if it can't start with a number
            if(!can_start_with_number) {
                char new_first_char = ' ';

                while(Character.isDigit(password.charAt(0))) {
                    if(!can_start_with_space) {
                        new_first_char = generateRandomChar(false, contains_upper_case_letters, contains_lower_case_letters, contains_special_characters, false);
                    } else {
                        new_first_char = generateRandomChar(false, contains_upper_case_letters, contains_lower_case_letters, contains_special_characters, contains_space);
                    }
                    password = password.substring(0, 0) +  new_first_char + password.substring(0 + 1);
                }

            }

            //replace first character if it can't start with a special character
            if(!can_start_with_special_char) {
                char new_first_char = ' ';
                int ascii_number = (int)(password.charAt(0));

                while((ascii_number>=32 & ascii_number<=47) | (ascii_number>=58 & ascii_number<=64) | (ascii_number>=91 & ascii_number<=96) | (ascii_number>=123 & ascii_number<=126)) {
                    if(!can_start_with_space) {
                        new_first_char = generateRandomChar(contains_digits, contains_upper_case_letters, contains_lower_case_letters, false, false);
                    } else if(!can_start_with_number) {
                        new_first_char = generateRandomChar(false, contains_upper_case_letters, contains_lower_case_letters, false, contains_space);
                    } else if((!can_start_with_space) & (!can_start_with_number)) {
                        new_first_char = generateRandomChar(false, contains_upper_case_letters, contains_lower_case_letters, false, false);
                    } else {
                        new_first_char = generateRandomChar(contains_digits, contains_upper_case_letters, contains_lower_case_letters, contains_special_characters, false);
                    }
                    password = password.substring(0, 0) +  new_first_char + password.substring(0 + 1);
                    ascii_number = (int)(password.charAt(0));
                }
                
            }

            //store password in array
            random_passwords[i] = password;
        }

        //print resulting passwords
        printPasswords(random_passwords);
    }

    //generates a random character given what type of character is needed
    public static char generateRandomChar(boolean contains_digits, boolean contains_upper_case_letters, boolean contains_lower_case_letters, boolean contains_special_characters, boolean contains_space) {

        //special charaters 1 [(space), '!', '"', '#', '$', '%', '&', ''', '(', ')', '*', '+', ',', '-', '.', '/'] are 32-47 in ASCII
        //digits 0-9 are 48-57 in ASCII
        //special characters 2 [':', ';', '<', '=', '>', '?', '@'] are 58-64 in ASCII
        //upper case letters are 65-90 in ASCII
        //special characters 3 ['[', '\', ']', '^', '_', '`'] are 91-96 in ASCII
        //lower case letters are 97-122 in ASCII
        //special characters 4 ['{', '|', '}', '~'] are 123-126 in ASCII

        //possible characters = 10 digits + 26  upper case + 26 lower case + (16 + 7 + 4) special characters = 89 possible characters

        //creates array with all possible ascii values
        int[] possible_ascii_chars = new int[] {};

        if(contains_digits) {
            int[] ascii_digits = new int[] {48, 49, 50, 51, 52, 53, 54, 55, 56, 57};

            int array_length1 = possible_ascii_chars.length;
            int array_length2 = ascii_digits.length;
            int[] concatenated_array = new int[array_length1 + array_length2];
            System.arraycopy(possible_ascii_chars, 0, concatenated_array, 0, array_length1);
            System.arraycopy(ascii_digits, 0, concatenated_array, array_length1, array_length2);
            possible_ascii_chars = concatenated_array;
        }
        if(contains_special_characters) {
            //remove ascii values for special characters not allowed in password
            int[] ascii_special_chars = new int[] {33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 58, 59, 60, 61, 62, 63, 64, 91, 92, 93, 94, 95, 96, 123, 124, 125, 126};

            int array_length1 = possible_ascii_chars.length;
            int array_length2 = ascii_special_chars.length;
            int[] concatenated_array = new int[array_length1 + array_length2];
            System.arraycopy(possible_ascii_chars, 0, concatenated_array, 0, array_length1);
            System.arraycopy(ascii_special_chars, 0, concatenated_array, array_length1, array_length2);
            possible_ascii_chars = concatenated_array;
        }
        if(contains_lower_case_letters) {
            int[] ascii_lower_case_letters = new int[] {97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};
            
            int array_length1 = possible_ascii_chars.length;
            int array_length2 = ascii_lower_case_letters.length;
            int[] concatenated_array = new int[array_length1 + array_length2];
            System.arraycopy(possible_ascii_chars, 0, concatenated_array, 0, array_length1);
            System.arraycopy(ascii_lower_case_letters, 0, concatenated_array, array_length1, array_length2);
            possible_ascii_chars = concatenated_array;
        }

        if(contains_upper_case_letters) {
            int[] ascii_upper_case_letters = new int[] {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90};
            
            int array_length1 = possible_ascii_chars.length;
            int array_length2 = ascii_upper_case_letters.length;
            int[] concatenated_array = new int[array_length1 + array_length2];
            System.arraycopy(possible_ascii_chars, 0, concatenated_array, 0, array_length1);
            System.arraycopy(ascii_upper_case_letters, 0, concatenated_array, array_length1, array_length2);
            possible_ascii_chars = concatenated_array;
        }

        if(contains_space) {
            int[] ascii_space = new int[] {32};

            int array_length1 = possible_ascii_chars.length;
            int array_length2 = ascii_space.length;
            int[] concatenated_array = new int[array_length1 + array_length2];
            System.arraycopy(possible_ascii_chars, 0, concatenated_array, 0, array_length1);
            System.arraycopy(ascii_space, 0, concatenated_array, array_length1, array_length2);
            possible_ascii_chars = concatenated_array;
        }

        //random number between 0 and length of possible ascii characters array
        int rand = (int)(Math.random()*possible_ascii_chars.length);

        return (char)(possible_ascii_chars[rand]);
    }

    //prints passwords stored in array
    public static void printPasswords(String[] random_passwords) {

        for(int i=1; i<random_passwords.length; i++) {
            System.out.println(random_passwords[i]);
        }
    }
}