package flashcards.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Daniel
 */
public class User {

    private String username, password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static boolean isDuplicateUser(String potentialUsername) {
        File userList = new File("users.txt");
        Scanner input;
        String usernameAndPassword, usernameOnly;

        if (!userList.exists()) {
            return false; // we have no users yet
        } else {
            try {
                input = new Scanner(userList);
                while (input.hasNextLine()) {
                    usernameAndPassword = input.nextLine();
                    usernameOnly = usernameAndPassword.split(" ")[0];
                    if (potentialUsername.equals(usernameOnly)) {
                        return true;
                    }
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return false; // this username was not found as duplicate
    }

    public boolean saveUsernameAndPassword() {
        File userList = new File("users.txt");
        Scanner input;
        PrintWriter output;
        String oldFileContents = "";

        if (userList.exists()) {
            try {
                input = new Scanner(userList);

                while (input.hasNextLine()) {
                    oldFileContents += input.nextLine() + "\n";
                }
            } catch (FileNotFoundException ex) {
                return false; // username, password not saved due to error
            }
        }

        try {
            output = new PrintWriter(userList);
            output.print(oldFileContents);
            output.println(username + " " + password);
            output.close();
            return true; // username, password saved
        } catch (FileNotFoundException ex) {
            return false; // username, password not saved due to error
        }
    }

    public static User login(String typedUsername, String typedPassword) {
        File userList = new File("users.txt");
        String[] usernamePasswordEntry;

        try {
            Scanner input = new Scanner(userList);

            while (input.hasNextLine()) {
                usernamePasswordEntry = input.nextLine().split(" ");
                if (typedUsername.equals(usernamePasswordEntry[0])) {
                    if (typedPassword.equals(usernamePasswordEntry[1])) {
                        return new User(typedUsername, typedPassword);
                    }
                }
            }

            return null; // we couldn't find username/password combo in file
        } catch (FileNotFoundException ex) {
            return null; // there are currently no registered users to login
        }
    }
}
