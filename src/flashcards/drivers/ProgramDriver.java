package flashcards.drivers;

import flashcards.model.FlashCard;
import flashcards.model.Subject;
import flashcards.model.User;
import java.util.ArrayList;
import java.util.Scanner;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Driver class which currently tests FlashCard, Subject, and User classes tied
 * together for meaningful command-line interface flash card program. There is
 * very limited handling of bad input in this class, as the class is temporary
 * and will be replaced by a GUI.
 *
 * @author Daniel Nedrow
 */
public class ProgramDriver {

    static Scanner keyboard = new Scanner(System.in);

    //variables for encryption
    private static final Random RANDOM = new SecureRandom();
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    /**
     * The "main" method of this class, to be invoked by project's Main class.
     * Centralizes console-interface testing of current FlashCard project.
     */
    public static void testProgramWithConsoleInterface() {

        User theUser = registerOrLogin(); // get user to register or login
        int option; // reusable variable to store user's console selections

        // keep running program until user selects "Quit" from outer menu
        while (true) {
            System.out.println("\nWould you like to choose a subject, delete a subject, or quit?\n"
                    + "1. Choose Subject\n2. Delete Subject\n3. Quit");
            System.out.print("Enter the number of the option you want: ");
            option = Integer.parseInt(keyboard.nextLine());

            if (option != 1 && option != 2) { // user chose to quit, so exit loop
                break;
            }

            if (option == 2) {
                deleteSubject(theUser);
            }

            // get user to choose or create a subject to work with
            if (option == 1) {
                Subject subject = chooseSubject(theUser);
                if (subject == null) {
                    break; // user didn't want to choose a subject, time to quit
                }

                System.out.println("You have chosen " + subject.getTitle());

                // user chooses to add flashcards, practice within this subject, or reset all of their scores
                thisSubject:
                while (true) {
                    System.out.println("\nWould you like to add flashcards or "
                            + "practice?\n1. Add flashcards\n2. Practice\n3. Delete flashcards\n4. Reset Score\n5. Return"
                            + " to previous menu");
                    System.out.print("Enter the number of the option you want: ");
                    option = Integer.parseInt(keyboard.nextLine());

                    switch (option) {
                        case 1:
                            addFlashCards(subject);
                            break;
                        case 2:
                            practice(subject);
                            break;
                        case 3:
                            removeFlashCard(subject);
                            System.out.println("flash card(s) removed!");
                            break;
                        case 4:
                            subject.resetFlashcards();
                            System.out.println("The scores for this subject have been reset!");
                            break;
                        default:
                            break thisSubject; // break out of loop with this label
                    }
                }
            } // end user choice if statement

        } // end outer menu loop

        theUser.saveUserState(); // save user's state before exiting
        System.out.println("\nAll of your work has been saved. See you next "
                + "time!");
    } // end testProgramWithConsoleInterface()

    /**
     * Method to allow user to practice knowledge of given subject by answering
     * flash cards.
     *
     * @param subject the subject the user is currently working with
     */
    public static void practice(Subject subject) {
        ArrayList<FlashCard> flashcards = subject.getFlashCards();
        int numFlashcards = subject.getNumFlashcards();
        int gradingChoice;
        String answer;
        char result;
        boolean correctness;
        FlashCard flashcard;

        if (numFlashcards == 0) {
            System.out.println("\nSorry, you have no flashcards in this "
                    + "subject.");
            return;
        }

        // find out if user wants answers auto-graded, or to grade self
        System.out.println("\nWould you like us to automatically check your "
                + "answer (must be spelled correctly), or do you want to "
                + "check the answer for us?");
        System.out.println("1. Please check my answer.\n2. I'll check it "
                + "myself.");
        System.out.print("Enter the number of the option you want: ");
        gradingChoice = Integer.parseInt(keyboard.nextLine());

        int flashcardIndex = 0;
        // continue asking FlashCard questions until user types "quit" as answer
        while (true) {
            // upon cycling through all FlashCards, remind user how to quit 
            if (flashcardIndex % numFlashcards == 0) {
                System.out.println("\nType QUIT for any answer to quit.");
            }

            // cycle through the flashcard array, don't go out-of-bounds
            flashcard = flashcards.get(flashcardIndex % numFlashcards);

            System.out.println("\n" + flashcard.getQuestion());
            System.out.print("Answer: ");
            answer = keyboard.nextLine();

            if (answer.equalsIgnoreCase("QUIT")) {
                break; // stop practicing these flashcards
            }

            if (gradingChoice == 1) { // user chose automatic grading
                boolean thisResult = flashcard.autoCheckCorrect(answer);
                if (thisResult) {
                    System.out.println("Correct!");
                } else {
                    System.out.println("Sorry, but the correct answer was: "
                            + flashcard.getAnswer());
                }
            } else { // user chose to grade self
                // Insist on 'C'orrect or 'I'ncorrect grade from user.
                while (true) {
                    System.out.println("The correct answer was: "
                            + flashcard.getAnswer());
                    System.out.print("Were you correct? Enter 'C' for Correct "
                            + "or 'I' for Incorrect: ");
                    result = keyboard.nextLine().charAt(0);
                    if (result == 'C' || result == 'I' || result == 'c'
                            || result == 'i') {
                        break; // good input, stop badgering user
                    } else {
                        System.out.println("You must enter 'C' or 'I'.");
                    }
                }

                // update user's right/wrong history for this flashcard
                correctness = (result == 'C' || result == 'c');
                flashcard.userChecksAnswer(correctness);
            } // end user self-grading choice block

            System.out.printf("Lifetime Percent Correct = %.2f%%\n",
                    flashcard.getPercentCorrect());
            System.out.printf("Last 5 Attempts Percent Correct = %.2f%%\n",
                    flashcard.getPercentCorrectLast5());
            flashcardIndex++; // prepare to ask question from next flash card
        } // end while loop of continuous flash card questions
    } // end practice method

    /**
     * Continuously add new FlashCards to a subject until user wants to stop.
     *
     * @param subject the subject to add FlashCards to
     */
    public static void addFlashCards(Subject subject) {
        String question, answer;

        // keeping prompting user for new question/answer pairs until they quit
        while (true) {
            System.out.print("\nEnter flashcard question or 'QUIT' to quit: ");
            question = keyboard.nextLine();
            if (question.equalsIgnoreCase("QUIT")) {
                return;
            } else {
                System.out.print("Enter flashcard answer: ");
                answer = keyboard.nextLine();
                subject.addFlashCard(question, answer);
            }
        }
    }

    public static void removeFlashCard(Subject subject) {
        ArrayList<FlashCard> flashcards = subject.getFlashCards();
        int numFlashcards = subject.getNumFlashcards();
        String answer;
        FlashCard flashcard;

        // continue asking user to delete flashcards until user types "quit" as answer
        while (numFlashcards > 0) {

            for (int i = 0; i < numFlashcards; i++) {
                flashcard = flashcards.get(i); // cycle through the flashcard array, don't go out-of-bounds

                System.out.println("\n" + flashcard.getQuestion());
                System.out.print("Do you want to delete this flash card? 'Y' or 'N' ");
                answer = keyboard.nextLine();

                if (answer.equalsIgnoreCase("Y")) {
                    subject.removeFlashcard(flashcard); // delete this flashcard
                    flashcards.remove(flashcard);
                    numFlashcards = subject.getNumFlashcards();
                    i--;
                }
            }
            System.out.println("Type 'QUIT' to quit or 'again' to keep deleting. ");
            answer = keyboard.nextLine();
            if (answer.equalsIgnoreCase("QUIT")) {
                break; // stop deleting flashcards
            }
        } // end while loop of continuous flash card questions
    } // end practice method

    /**
     * Prompt user to choose a subject to work with or create a new one, and
     * return chosen/created subject to caller.
     *
     * @param theUser the user making the choice
     * @return the subject the user has chosen to work with or has newly created
     */
    public static Subject chooseSubject(User theUser) {
        int subjectChoice;
        String subjectTitle;

        System.out.println("\n1. Create New Subject");
        ArrayList<Subject> subjects = theUser.getSubjects(); // user's subjects

        // display list of available subjects
        int index;
        for (index = 0; index < theUser.getNumSubjects(); index++) {
            System.out.println((index + 2) + ". "
                    + subjects.get(index).getTitle());
        }

        System.out.println((index + 2) + ". Quit"); // display quit option
        System.out.print("Enter the number of the option you want: ");
        // adjust for fact that displayed numbers don't match subj. array index
        subjectChoice = Integer.parseInt(keyboard.nextLine()) - 2;

        if (subjectChoice == -1) { // user chose 1. Create New Subject
            System.out.print("Enter subject title: ");
            subjectTitle = keyboard.nextLine();
            Subject newSubject = new Subject(subjectTitle);
            theUser.addSubject(newSubject);
            return newSubject;
        } else if (subjectChoice < index && subjectChoice > -1) {
            return subjects.get(subjectChoice); // user chose existing subject
        } else { // user chose to quit, return no subject
            return null;
        }
    }

    public static void deleteSubject(User theUser) {
        int subjectChoice;
        ArrayList<Subject> subjects = theUser.getSubjects(); // user's subjects

        // display list of available subjects
        int index;
        for (index = 0; index < theUser.getNumSubjects(); index++) {
            System.out.println((index + 1) + ". "
                    + subjects.get(index).getTitle());
        }

        System.out.print("Enter the subject number you want to delete or type '0' to quit ");
        // adjust for fact that displayed numbers don't match subj. array index
        subjectChoice = Integer.parseInt(keyboard.nextLine()) - 1;

        if (subjectChoice < index && subjectChoice > -1) {
            System.out.println(subjects.get(subjectChoice).getTitle() + " has been deleted!");
            theUser.deleteSubject(subjects.get(subjectChoice)); // user chose existing subject

            // user chose to quit, return no subject
        }
    }

    /**
     * Get user to register or login.
     *
     * @return User object with user's saved Subjects, FlashCards, history
     */
    public static User registerOrLogin() {
        String userAccessChoice, username, password;
        User theUser = null;
        boolean loggedIn = false;

        // keep prompting register/login until valid credentials given
        while (!loggedIn) {
            System.out.print("\nPlease type 'register' or 'login': ");
            userAccessChoice = keyboard.next();

            if (userAccessChoice.equalsIgnoreCase("register")) {
                System.out.print("Enter desired username: ");
                username = keyboard.next();
                System.out.print("Enter desired password: ");
                password = keyboard.next();

                // We are not currently ready to use the encrypt functionality
                //String securePassword = encrypt(password);
                // check if username taken before user is registered
                if (User.isDuplicateUser(username)) {
                    System.out.println("Sorry, that username is taken.");
                } else { // register user

                    theUser = new User(username, password);
                    if (theUser.saveUsernameAndPassword()) {
                        System.out.println("Username, password successfuly "
                                + "created.");
                        loggedIn = true; // User created, break from prompt loop
                    } else {
                        System.out.println("There was a problem and we couldn't"
                                + " save your username and password.");
                    }

                }
            } else if (userAccessChoice.equalsIgnoreCase("login")) { // login
                System.out.print("Enter username: ");
                username = keyboard.next();
                System.out.print("Enter password: ");
                password = keyboard.next();
                theUser = User.login(username, password); // attempt user login

                if (theUser == null) { // credentials wrong, inform user
                    System.out.println("Incorrect username, password "
                            + "combination.");
                } else { // credentials matched, user is logged in
                    System.out.println("You are logged in!");
                    loggedIn = true; // break out of prompting loop
                }
            } else { // invalid choice given, reprompt
                System.out.println("Please re-enter your choice.");
            }
        }

        keyboard.nextLine(); // purge input buffer
        return theUser;
    }

    //this method takes a string and encrypts it
    //by using the getSalt() and hash() methods then returns an
    //encrypted String
    public static String encrypt(String textToEncrypt) {
        byte[] salt = getSalt(); //generate salt
        byte[] hash = hash(textToEncrypt.toCharArray(), salt);
        String hashString = "";
        for (int i = 0; i < hash.length; i++) {
            hashString += (char) hash[i];
        }
        return hashString;
    }

    //this method returns 16 byte random salt value
    public static byte[] getSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }

    //this method returns a hashed and salted password
    public static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        Arrays.fill(password, Character.MIN_VALUE);

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return skf.generateSecret(spec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
        } finally {
            spec.clearPassword();
        }

    }

    //this method generates a password 
    public static String generateSecurePassword(String password, String salt) {
        String returnValue = null;

        byte[] securePassword = hash(password.toCharArray(), salt.getBytes());

        returnValue = Base64.getEncoder().encodeToString(securePassword);

        return returnValue;
    }

    //this method verifies users password
    public static boolean verifyUserPassword(String providedPassword,
            String securedPassword,
            String salt) {
        boolean returnValue = false;

        //generate new secure password with the same salt value
        String newSecurePassword = generateSecurePassword(providedPassword, salt);

        //check if two passwords are the same
        returnValue = newSecurePassword.equalsIgnoreCase(securedPassword);

        return returnValue;
    }

}
