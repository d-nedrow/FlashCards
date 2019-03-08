package flashcards.model;

import java.util.ArrayList;

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
}
