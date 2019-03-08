package flashcards.model;

/**
 * A class which models a flash card.
 *
 * @author Daniel Nedrow
 */
public class FlashCard {

    private static final String DELIM = " @DL ";
    private static final int QUESTION = 0, ANSWER = 1, NUM_ATTEMPTS = 2,
            NUM_CORRECT = 3, NUM_INCORRECT = 4, LAST_5_ATTEMPTS = 5;

    private String question, answer;
    private int numAttempts, numCorrect, numIncorrect;
    private int[] last5Attempts; // "1" for correct, "0" for incorrect

    /**
     * Create new FlashCard with given question and answer.
     *
     * @param question flash card question
     * @param answer flash card answer
     */
    public FlashCard(String question, String answer) {
        this.question = question;
        this.answer = answer;
        numAttempts = numCorrect = numIncorrect = 0;
        last5Attempts = new int[5];
    }

    /**
     * @return the flash card's question
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @return the flash card's answer
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Set the flash card's question.
     *
     * @param question the question to assign to this flash card
     */
    public void setQuestion(String question) {
        this.question = question;
    }

    /**
     * Set the flash card's answer
     *
     * @param answer the answer to assign to this flash card
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return the number of times user has attempted to answer this flash card
     */
    public int getNumAttempts() {
        return numAttempts;
    }

    /**
     * @return the number of times user has answered this flash card correctly
     */
    public int getNumCorrect() {
        return numCorrect;
    }

    /**
     * @return the number of times user has answered this flash card incorrectly
     */
    public int getNumIncorrect() {
        return numIncorrect;
    }

    /**
     * Set the number of times the user has attempted this flash card.
     *
     * @param numAttempts number of times user has attempted this flash card
     */
    public void setNumAttempts(int numAttempts) {
        this.numAttempts = numAttempts;
    }

    /**
     * Set number of times the user has answered this flash card correctly.
     *
     * @param numCorrect number of user's correct answers for flash card
     */
    public void setNumCorrect(int numCorrect) {
        this.numCorrect = numCorrect;
    }

    /**
     * Set number of times user has answered this flash card incorrectly.
     *
     * @param numIncorrect number of user's incorrect answers for flash card
     */
    public void setNumIncorrect(int numIncorrect) {
        this.numIncorrect = numIncorrect;
    }

    /**
     * @return right/wrong history of user's last 5 attempts
     */
    public int[] getLast5Attempts() {
        return last5Attempts;
    }

    /**
     * Set user's right/wrong history of last 5 attempts on flash card.
     *
     * @param last5 array with right/wrong history of last 5 attempts
     */
    public void setLast5Attempts(int[] last5) {
        last5Attempts = last5;
    }

    /**
     * Checks if user's answer is correct with String equality, and updates
     * user's right/wrong history.
     *
     * @param attempt the user's answer
     * @return true if correct, false otherwise
     */
    public boolean autoCheckCorrect(String attempt) {
        boolean correct;
        if (answer.equalsIgnoreCase(attempt)) {
            numCorrect++;
            last5Attempts[numAttempts % 5] = 1;
            correct = true;
        } else {
            numIncorrect++;
            last5Attempts[numAttempts % 5] = 0;
            correct = false;
        }

        numAttempts++;
        return correct;
    }

    /**
     * Accepts a boolean representing user's judgment on whether they got
     * question right or wrong. For answers too complicated for String match.
     * Updates user's right/wrong history.
     *
     * @param correct user's judgment whether question was correct or not
     */
    public void userChecksAnswer(boolean correct) {
        if (correct) {
            numCorrect++;
            last5Attempts[numAttempts % 5] = 1;
        } else {
            numIncorrect++;
            last5Attempts[numAttempts % 5] = 0;
        }

        numAttempts++;
    }

    /**
     * @return lifetime percentage of correct answers for this flash card
     */
    public double getPercentCorrect() {
        if (numAttempts == 0) {
            return 0;
        }

        return (numCorrect * 1.0 / numAttempts) * 100;
    }

    /**
     * @return percentage of correct answers over last 5 attempts
     */
    public double getPercentCorrectLast5() {
        if (numAttempts == 0) {
            return 0;
        }

        int sum = 0;
        for (int i = 0; i < 5 && i < numAttempts; i++) {
            sum += last5Attempts[i];
        }

        int divisor = Math.min(numAttempts, 5); // user may not have 5 attempts
        return (sum * 1.0 / divisor) * 100;
    }

    /**
     * Gives a String with all the data of a FlashCard, suitable for save file.
     * This method is the inverse of the fromString method in that it produces a
     * String format of a FlashCard which fromString can use to create the
     * FlashCard.
     *
     * @return a String representing the FlashCard
     */
    @Override
    public String toString() {
        StringBuilder flashcardString = new StringBuilder(question + DELIM
                + answer + DELIM + numAttempts + DELIM + numCorrect + DELIM
                + numIncorrect + DELIM);

        for (int i = 0; i < 5; i++) {
            flashcardString.append(last5Attempts[i]).append(" ");
        }

        flashcardString.append("\n");
        return flashcardString.toString();
    }

    /**
     * This method is the inverse of the toString method in that it takes the
     * toString format of a FlashCard and returns the FlashCard object.
     *
     * @param flashcardString a String with all the data for a FlashCard
     * @return the FlashCard with its data loaded from flashcardString
     */
    public static FlashCard fromString(String flashcardString) {
        String[] data = flashcardString.split(DELIM);
        FlashCard flashcard = new FlashCard(data[QUESTION], data[ANSWER]);
        flashcard.setNumAttempts(Integer.parseInt(data[NUM_ATTEMPTS]));
        flashcard.setNumCorrect(Integer.parseInt(data[NUM_CORRECT]));
        flashcard.setNumIncorrect(Integer.parseInt(data[NUM_INCORRECT]));

        String[] last5 = data[LAST_5_ATTEMPTS].split(" ");
        int[] last5Atmpts = new int[5];
        for (int i = 0; i < 5; i++) {
            last5Atmpts[i] = Integer.parseInt(last5[i]);
        }
        flashcard.setLast5Attempts(last5Atmpts);

        return flashcard;
    }
}
