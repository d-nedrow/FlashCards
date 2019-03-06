package flashcards.drivers;

import flashcards.model.FlashCard;
import flashcards.model.Subject;
import flashcards.model.User;
import java.util.Scanner;

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

    /**
     * The "main" method of this class, to be invoked by project's Main class.
     * Centralizes console-interface testing of current FlashCard project.
     */
    public static void testProgramWithConsoleInterface() {
        User theUser = registerOrLogin(); // get user to register or login
        int option; // reusable variable to store user's console selections

        // keep running program until user selects "Quit" from outer menu
        while (true) {
            System.out.println("\nWould you like to choose a subject or quit?\n"
                    + "1. Choose Subject\n2. Quit");
            System.out.print("Enter the number of the option you want: ");
            option = Integer.parseInt(keyboard.nextLine());

            if (option != 1) { // user chose to quit, so exit loop
                break;
            }

            // get user to choose or create a subject to work with
            Subject subject = chooseSubject(theUser);
            if (subject == null) {
                break; // user didn't want to choose a subject, time to quit
            }

            System.out.println("You have chosen " + subject.getTitle());

            // user chooses to add flashcards or practice within this subject
            thisSubject:
            while (true) {
                System.out.println("\nWould you like to add flashcards or "
                        + "practice?\n1. Add flashcards\n2. Practice\n3. Return"
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
                    default:
                        break thisSubject; // break out of loop with this label
                }
            }
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
        FlashCard[] flashcards = subject.getFlashCards();
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
            flashcard = flashcards[flashcardIndex % numFlashcards];

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
        Subject[] subjects = theUser.getSubjects(); // user's current subjects

        // display list of available subjects
        int index;
        for (index = 0; index < theUser.getNumSubjects(); index++) {
            System.out.println((index + 2) + ". " + subjects[index].getTitle());
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
            return subjects[subjectChoice]; // user chose existing subject
        } else { // user chose to quit, return no subject
            return null;
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
}
