package flashcards.drivers;

import flashcards.model.User;
import java.util.Scanner;

/**
 *
 * @author Daniel
 */
public class ProgramDriver {

    static Scanner keyboard = new Scanner(System.in);

    public static void testProgramWithConsoleInterface() {
        User theUser = registerOrLogin();
        theUser.saveUserState();
    }

    public static User registerOrLogin() {
        String userAccessChoice, username, password;
        User theUser = null;
        boolean loggedIn = false;

        while (!loggedIn) {
            System.out.print("\nPlease type 'register' or 'login': ");
            userAccessChoice = keyboard.next();

            if (userAccessChoice.equalsIgnoreCase("register")) {
                System.out.print("Enter desired username: ");
                username = keyboard.next();
                System.out.print("Enter desired password: ");
                password = keyboard.next();

                if (User.isDuplicateUser(username)) {
                    System.out.println("Sorry, that username is taken.");
                } else {
                    theUser = new User(username, password);
                    if (theUser.saveUsernameAndPassword()) {
                        System.out.println("Username, password successfuly "
                                + "created.");
                        loggedIn = true;
                    } else {
                        System.out.println("There was a problem and we couldn't"
                                + " save your username and password.");
                    }

                }
            } else if (userAccessChoice.equalsIgnoreCase("login")) {
                System.out.print("Enter username: ");
                username = keyboard.next();
                System.out.print("Enter password: ");
                password = keyboard.next();
                theUser = User.login(username, password);

                if (theUser == null) {
                    System.out.println("Incorrect username, password "
                            + "combination.");
                } else {
                    System.out.println("You are logged in!");
                    loggedIn = true;
                }
            } else {
                System.out.println("Please re-enter your choice.");
            }
        }

        return theUser;
    }
}
