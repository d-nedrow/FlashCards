package flashcards.model;

/**
 * A class which models a flash card.
 *
 * @author Daniel Nedrow
 */
public class FlashCard {

    private String question, answer;
    private int[] rightWrongHistory; // "1" for each correct, "0" for incorrect
    private int numAttempts;

    /**
     * Create new FlashCard with given question and answer.
     *
     * @param question flash card question
     * @param answer flash card answer
     */
    public FlashCard(String question, String answer) {
        this.question = question;
        this.answer = answer;
        rightWrongHistory = new int[100];
        numAttempts = 0;
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
     * @return the number of times user has attempted to answer this flash card
     */
    public int getNumAttempts() {
        return numAttempts;
    }

    /**
     * @return An array with user's history of correct, incorrect answers. "1"
     * for each correct answer, "0" for each incorrect answer.
     */
    public int[] getRightWrongHistory() {
        return rightWrongHistory;
    }

    /**
     * Takes in single String of 1's and 0's and converts those numbers into the
     * the user's right/wrong history. For loading saved user state.
     *
     * @param numbers the String of 1's and 0's to be converted to int[]
     */
    public void setRightWrongHistory(String numbers) {
        String[] history = numbers.split(" ");

        for (int i = 0; i < history.length; i++) {
            rightWrongHistory[i] = Integer.parseInt(history[i]);
            numAttempts++;
        }
    }

    /**
     * Checks if user's answer is correct with String equality, and updates
     * user's right/wrong history.
     *
     * @param attempt the user's answer
     * @return true if correct, false otherwise
     */
    public boolean autoCheckCorrect(String attempt) {
        if (answer.equalsIgnoreCase(attempt)) {
            rightWrongHistory[numAttempts++] = 1;
            return true;
        } else {
            rightWrongHistory[numAttempts++] = 0;
            return false;
        }
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
            rightWrongHistory[numAttempts++] = 1;
        } else {
            rightWrongHistory[numAttempts++] = 0;
        }
    }

    /**
     * @return lifetime percentage of correct answers for this flash card
     */
    public double getPercentCorrect() {
        int sum = 0;

        for (int i = 0; i < numAttempts; i++) {
            sum += rightWrongHistory[i];
        }

        return (sum * 1.0 / numAttempts) * 100;
    }

    /**
     * @return percentage of correct answers over last 5 attempts
     */
    public double getPercentCorrectLast5() {
        int sum = 0;
        int divisor = Math.min(numAttempts, 5); // user may not have 5 attempts

        int i = Math.max(numAttempts - 5, 0); // protect from less than 5 tries
        for (; i < numAttempts; i++) {
            sum += rightWrongHistory[i];
        }

        return (sum * 1.0 / divisor) * 100;
    }
}
