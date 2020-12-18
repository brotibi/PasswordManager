package lib;

//Author: Justin Raynor
//Public class that creates a user defined number of passwords of user defined strength,
//checks if the password is unique and returns the password to the user.
//TODO: create GUI for user to define values for the specified fields below.

public class PasswordGenerator {
    //length of password
    public int length = 18;

    // What characters may appear in the password
    public boolean contains_digits = true;
    public boolean contains_upper_case_letters = true;
    public boolean contains_lower_case_letters = true;
    public boolean contains_special_characters = true;
    public boolean contains_space = true;

    // password rules (user can set each depending on the password requirement)
    public boolean can_start_with_number = false;
    public boolean can_start_with_special_char = false;
    public boolean can_start_with_space = false;
    public boolean contains_at_least_two_letters = true;
    public boolean contains_at_least_two_num = true;
    public boolean contains_at_least_two_special_char = true;

    public static void main(String[] args) {
        //number of passwords to be generated
        int num_passwords = 10;
        PasswordGenerator gen = new PasswordGenerator();

        //generate and store random passwords that meet all requirements above
        String[] random_passwords = new String[num_passwords];
        for (int i = 0; i < num_passwords; ++i) {
            random_passwords[i] = gen.generate();
        }

        //print resulting passwords
        printPasswords(random_passwords);
    }

    private boolean isSpecialChar(char c) {
        return (c >= 32 && c <= 47) || (c >= 58 && c <= 64) || (c >= 91 && c <= 96) || (c >= 123 && c <= 126);
    }

    public String generate() {
        StringBuilder password = new StringBuilder();

        for (int j = 0; j < length; j++) {
            password.append(generateRandomChar(contains_digits, contains_upper_case_letters, contains_lower_case_letters, contains_special_characters, contains_space));
        }

        //check if password meets rules
        if ((!can_start_with_space && password.charAt(0) == ' ') ||
                (!can_start_with_number && Character.isDigit(password.charAt(0))) ||
                (!can_start_with_special_char && isSpecialChar(password.charAt(0)))) {
            password.setCharAt(0, generateRandomChar(can_start_with_number, contains_upper_case_letters, contains_lower_case_letters, can_start_with_special_char, can_start_with_space));
        }

        return password.toString();
    }

    private int[] concatenate(int[] arr1, int[] arr2) {
        int len1 = arr1.length;
        int len2 = arr2.length;

        int[] out = new int[len1 + len2];
        System.arraycopy(arr1, 0, out, 0, len1);
        System.arraycopy(arr2, 0, out, len1, len2);

        return out;
    }

    //generates a random character given what type of character is needed
    private char generateRandomChar(boolean contains_digits, boolean contains_upper_case_letters, boolean contains_lower_case_letters, boolean contains_special_characters, boolean contains_space) {

        //special charaters 1 [(space), '!', '"', '#', '$', '%', '&', ''', '(', ')', '*', '+', ',', '-', '.', '/'] are 32-47 in ASCII
        //digits 0-9 are 48-57 in ASCII
        //special characters 2 [':', ';', '<', '=', '>', '?', '@'] are 58-64 in ASCII
        //upper case letters are 65-90 in ASCII
        //special characters 3 ['[', '\', ']', '^', '_', '`'] are 91-96 in ASCII
        //lower case letters are 97-122 in ASCII
        //special characters 4 ['{', '|', '}', '~'] are 123-126 in ASCII

        //possible characters = 10 digits + 26  upper case + 26 lower case + (16 + 7 + 4) special characters = 89 possible characters

        //creates array with all possible ascii values
        int[] possible_ascii_chars = new int[]{};

        if (contains_digits) {
            int[] ascii_digits = new int[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57};
            possible_ascii_chars = concatenate(possible_ascii_chars, ascii_digits);
        }
        if (contains_special_characters) {
            //remove ascii values for special characters not allowed in password
            int[] ascii_special_chars = new int[]{33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 58, 59, 60, 61, 62, 63, 64, 91, 92, 93, 94, 95, 96, 123, 124, 125, 126};
            possible_ascii_chars = concatenate(possible_ascii_chars, ascii_special_chars);
        }
        if (contains_lower_case_letters) {
            int[] ascii_lower_case_letters = new int[]{97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};
            possible_ascii_chars = concatenate(possible_ascii_chars, ascii_lower_case_letters);
        }
        if (contains_upper_case_letters) {
            int[] ascii_upper_case_letters = new int[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90};
            possible_ascii_chars = concatenate(possible_ascii_chars, ascii_upper_case_letters);
        }
        if (contains_space) {
            int[] ascii_space = new int[]{32};
            possible_ascii_chars = concatenate(possible_ascii_chars, ascii_space);
        }

        //random number between 0 and length of possible ascii characters array
        int rand = (int) (Math.random() * possible_ascii_chars.length);

        return (char) (possible_ascii_chars[rand]);
    }

    //prints passwords stored in array
    public static void printPasswords(String[] random_passwords) {
        for (String random_password : random_passwords) {
            System.out.println(random_password);
        }
    }
}