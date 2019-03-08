package flashcards.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class which models a user of the FlashCards program. Collects all of the
 * user's subjects and flash cards in one place, and allows for saving and
 * loading this information from file.
 *
 * @author Daniel Nedrow
 */
public class User {

    private String username, password;
    private Subject[] subjects;
    private int numSubjects;

    /**
     * Creates new user with given username and password.
     *
     * @param username user's username
     * @param password user's password
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        subjects = new Subject[10];
    }

    /**
     * @return array containing user's subjects
     */
    public Subject[] getSubjects() {
        return subjects;
    }

    /**
     * Adds a given subject for this user.
     *
     * @param subject subject to be added for this user
     */
    public void addSubject(Subject subject) {
        subjects[numSubjects++] = subject;
    }

    /**
     * @return the current number of subjects for this user
     */
    public int getNumSubjects() {
        return numSubjects;
    }

    /**
     * Save the user's subjects, flash cards, and right/wrong history to file.
     */
    public void saveUserState() {
        // make a new directory (if it doesn't exist) to hold user save files
        new File(getPath() + "/userStates").mkdirs();

        String filename = getPath() + "/userStates/" + username + ".txt";
        PrintWriter output;
        FlashCard[] flashcards;

        try {
            output = new PrintWriter(filename);

            // output each of user's subjects to file
            for (int i = 0; i < numSubjects; i++) {
                output.println("Subject: " + subjects[i].getTitle());

                // output each of subject's flash cards to file
                flashcards = subjects[i].getFlashCards();
                for (int j = 0; j < subjects[i].getNumFlashcards(); j++) {
                    output.print(flashcards[j]);
                }
            }

            output.close();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Load the user's subjects, flash cards, and right/wrong history from file.
     */
    public void loadUserState() {
        String filename = getPath() + "/userStates/" + username + ".txt";
        File userState = new File(filename);
        Scanner input;
        String inputLine;
        FlashCard flashcard;

        try {
            input = new Scanner(userState);

            // read each line of user's save file to load user's state
            while (input.hasNextLine()) {
                inputLine = input.nextLine();
                if (inputLine.startsWith("Subject: ")) {
                    // ignore "Subject: " and just load the subject's title
                    inputLine = inputLine.substring("Subject: ".length());
                    addSubject(new Subject(inputLine));
                } else {
                    // the input line has the data to create a flash card
                    flashcard = FlashCard.fromString(inputLine);
                    subjects[numSubjects - 1].addFlashCard(flashcard);
                }
            }
        } catch (FileNotFoundException ex) {
            try {
                userState.createNewFile(); // if the file didn't exist, create
            } catch (IOException ex1) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null,
                        ex1);
            }
        }
    }

    /**
     * Checks if username is already taken. Used during user registration.
     *
     * @param potentialUsername user's desired username
     * @return true if username already taken, false otherwise
     */
    public static boolean isDuplicateUser(String potentialUsername) {
        File userList = new File(getPath() + "/users.txt");
        Scanner input;
        String usernameAndPassword, usernameOnly;

        if (!userList.exists()) {
            return false; // this is the first user created, no worries
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
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null,
                        ex);
            }
        }

        return false; // this username was not found as duplicate
    }

    /**
     * Save user's registered username and password to file users.txt
     *
     * @return true if successfully saved, false otherwise
     */
    public boolean saveUsernameAndPassword() {
        File userList = new File(getPath() + "/users.txt");
        PrintWriter output;
        String oldFileContents = "";

        // if there is already users.txt, hold old content to copy to new file
        if (userList.exists()) {
            oldFileContents = getOldFileContents(userList);
        }

        try {
            output = new PrintWriter(userList);
            output.print(oldFileContents); // copy previous user list to file
            output.println(username + " " + password); // add this user
            output.close();
            return true; // username, password saved
        } catch (FileNotFoundException ex) {
            return false; // username, password not saved due to error
        }
    }

    /**
     * Allows user to log in with existing username, password. If credentials
     * are correct, load user's state.
     *
     * @param typedUsername username that user is attempting to login with
     * @param typedPassword password that user is attempting to login with
     * @return User object containing this user's flash cards, subjects, history
     */
    public static User login(String typedUsername, String typedPassword) {
        File userList = new File(getPath() + "/users.txt");
        String[] usernamePasswordEntry;
        User theUser;

        try {
            Scanner input = new Scanner(userList);

            // check users.txt to see if provided credentials are correct
            while (input.hasNextLine()) {
                usernamePasswordEntry = input.nextLine().split(" ");
                if (typedUsername.equals(usernamePasswordEntry[0])) {
                    if (typedPassword.equals(usernamePasswordEntry[1])) {
                        theUser = new User(typedUsername, typedPassword);
                        theUser.loadUserState(); // load user's flash cards etc.
                        return theUser;
                    }
                }
            }

            return null; // we couldn't find username/password combo in file
        } catch (FileNotFoundException ex) {
            return null; // there are currently no registered users to login
        }
    }

    /**
     * Helper method to get old contents from file (so that they can later be
     * copied to newly written file, and not simply be overwritten).
     *
     * @param file file with old contents we want to keep
     * @return a String containing all contents of file
     */
    public static String getOldFileContents(File file) {
        Scanner input;
        String oldFileContents = "";

        try {
            input = new Scanner(file);

            while (input.hasNextLine()) {
                oldFileContents += input.nextLine() + "\n";
            }

            input.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }

        return oldFileContents;
    }

    /**
     * @return the current working directory, only up to and including project's
     * root FlashCards folder (e.g. C:/userSpecificDirectory/FlashCards)
     */
    public static String getPath() {
        String path = (System.getProperty("user.dir"));
        return path.substring(0, path.indexOf("FlashCards")
                + "FlashCards".length());
    }
}
