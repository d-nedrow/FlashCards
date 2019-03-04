package flashcards.model;

/**
 *
 * @author Daniel
 */
public class Subject {
    String title;
    Subject[] subTopics;
    FlashCard[] flashcards;
    int numSubjects, numFlashcards;
    
    public Subject(String title) {
        this.title = title;
        subTopics = new Subject[10];
        flashcards = new FlashCard[100];
        numSubjects = numFlashcards = 0;
    }
    
    public void addSubject(String title) {
        subTopics[numSubjects++] = new Subject(title);
    }
    
    public void addFlashCard(String question, String answer) {
        flashcards[numFlashcards++] = new FlashCard(question, answer);
    }
    
    
}
