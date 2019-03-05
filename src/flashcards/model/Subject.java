package flashcards.model;

/**
 *
 * @author Daniel
 */
public class Subject {
    private String title;
    private FlashCard[] flashcards;
    private int numFlashcards;
    
    public Subject(String title) {
        this.title = title;
        flashcards = new FlashCard[100];
        numFlashcards = 0;
    }
    
    public String getTitle() {
        return title;
    }
    
    public FlashCard[] getFlashCards() {
        return flashcards;
    }
    
    public void addFlashCard(String question, String answer) {
        flashcards[numFlashcards++] = new FlashCard(question, answer);
    }
    
    public void addFlashCard(FlashCard flashcard) {
        flashcards[numFlashcards++] = flashcard;
    }
    
    public int getNumFlashcards() {
        return numFlashcards;
    }
    
    
}
