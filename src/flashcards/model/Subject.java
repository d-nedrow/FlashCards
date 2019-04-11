package flashcards.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

/**
 * A class which models a subject or topic to be filled with FlashCards.
 *
 * @author Daniel Nedrow
 */
public class Subject {

    private String title;
    private ArrayList<FlashCard> flashcards;
    private int numFlashcards;

    /**
     * Create new subject with given title.
     *
     * @param title subject's title
     */
    public Subject(String title) {
        this.title = title;
        flashcards = new ArrayList<>();
        numFlashcards = 0;
    }

    /**
     * @return the subject's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the subject's ArrayList of FlashCards
     */
    public ArrayList<FlashCard> getFlashCards() {
        return flashcards;
    }

    /**
     * Add FlashCard to subject with given question and answer.
     *
     * @param question flash card question
     * @param answer flash card answer
     */
    public void addFlashCard(String question, String answer) {
        flashcards.add(new FlashCard(question, answer));
        numFlashcards++;
    }

    /**
     * Add given FlashCard object to this subject.
     *
     * @param flashcard FlashCard to add to subject
     */
    public void addFlashCard(FlashCard flashcard) {
        flashcards.add(flashcard);
        numFlashcards++;
    }

    /**
     * @return the number of flash cards in this subject
     */
    public int getNumFlashcards() {
        return numFlashcards;
    }

    /**
     * cycles through the array list clearing each card's score
     */
    public void resetFlashcards() {
        for (FlashCard card : flashcards) {
            card.resetScore();
        }
    }

    public void removeFlashcard(FlashCard card) {
        flashcards.remove(card);
        numFlashcards--;
    }

    public void shuffleFlashcards() {
        Collections.shuffle(flashcards);
    }

    /**
     * Creates a new subject filled with flash cards by reading a properly-
     * formatted file (specified as the parameter).
     *
     * @param subjectFile the specified file to read
     * @return the new subject created by reading the file
     */
    public static Subject createSubjectFromFile(File subjectFile) {
        String[] questionAnswer;
        Subject subject;
        String title;

        try {
            Scanner input = new Scanner(subjectFile);
            title = input.nextLine();
            subject = new Subject(title);

            while (input.hasNextLine()) {
                questionAnswer = input.nextLine().split("@DL");
                subject.addFlashCard(questionAnswer[0].trim(),
                        questionAnswer[1].trim());
            }

            return subject;
        } catch (FileNotFoundException ex) {
            return null;
        }
    }
}
