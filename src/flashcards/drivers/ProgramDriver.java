package flashcards.drivers;

import flashcards.model.FlashCard;
import flashcards.model.Subject;
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
        int option;

        while (true) {
            System.out.println("\nWould you like to choose a subject or quit?\n"
                    + "1. Choose Subject\n2. Quit");
            System.out.print("Enter the number of the option you want: ");
            option = Integer.parseInt(keyboard.nextLine());

            if (option != 1) {
                break;
            }

            Subject subject = chooseSubject(theUser);
            if (subject == null) {
                break;
            }

            System.out.println("You have chosen " + subject.getTitle());

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
                        break thisSubject;
                }
            }
        }

        theUser.saveUserState();
        System.out.println("\nAll of your work has been saved. See you next "
                + "time!");
    }

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

        System.out.println("\nWould you like us to automatically check your "
                + "answer (must be spelled correctly), or do you want to "
                + "check the answer for us?");
        System.out.println("1. Please check my answer.\n2. I'll check it "
                + "myself.");
        System.out.print("Enter the number of the option you want: ");
        gradingChoice = Integer.parseInt(keyboard.nextLine());

        int flashcardIndex = 0;
        while (true) {
            if (flashcardIndex % numFlashcards == 0) {
                System.out.println("\nType QUIT for any answer to quit.");
            }

            flashcard = flashcards[flashcardIndex % numFlashcards];
            System.out.println("\n" + flashcard.getQuestion());
            System.out.print("Answer: ");
            answer = keyboard.nextLine();

            if (answer.equalsIgnoreCase("QUIT")) {
                break; // stop practicing these flashcards
            }

            if (gradingChoice == 1) {
                boolean thisResult = flashcard.autoCheckCorrect(answer);
                if (thisResult) {
                    System.out.println("Correct!");
                } else {
                    System.out.println("Sorry, but the correct answer was: "
                            + flashcard.getAnswer());
                }
            } else {
                while (true) {
                    System.out.println("The correct answer was: "
                            + flashcard.getAnswer());
                    System.out.print("Were you correct? Enter 'C' for Correct "
                            + "or 'I' for Incorrect: ");
                    result = keyboard.nextLine().charAt(0);
                    if (result == 'C' || result == 'I' || result == 'c'
                            || result == 'i') {
                        break;
                    } else {
                        System.out.println("You must enter 'C' or 'I'.");
                    }
                }

                correctness = (result == 'C' || result == 'c');
                flashcard.userChecksAnswer(correctness);
            }

            System.out.printf("Lifetime Percent Correct = %.2f%%\n",
                    flashcard.getPercentCorrect());
            System.out.printf("Last 5 Attempts Percent Correct = %.2f%%\n",
                    flashcard.getPercentCorrectLast5());
            flashcardIndex++;
        }
    }

    public static void addFlashCards(Subject subject) {
        String question, answer;

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

    public static Subject chooseSubject(User theUser) {
        int subjectChoice;
        String subjectTitle;

        System.out.println("\n1. Create New Subject");
        Subject[] subjects = theUser.getSubjects();

        int index;
        for (index = 0; index < theUser.getNumSubjects(); index++) {
            System.out.println((index + 2) + ". " + subjects[index].getTitle());
        }

        System.out.println((index + 2) + ". Quit");
        System.out.print("Enter the number of the option you want: ");

        subjectChoice = Integer.parseInt(keyboard.nextLine()) - 2;

        if (subjectChoice == -1) {
            System.out.print("Enter subject title: ");
            subjectTitle = keyboard.nextLine();
            Subject newSubject = new Subject(subjectTitle);
            theUser.addSubject(newSubject);
            return newSubject;
        } else if (subjectChoice < index && subjectChoice > -1) {
            return subjects[subjectChoice];
        } else {
            return null;
        }
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

        keyboard.nextLine(); // purge input buffer
        return theUser;
    }
}
