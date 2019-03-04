package flashcards.model;

/**
 *
 * @author Daniel
 */
public class FlashCard {
    private String question, answer;
    private int[] rightWrongHistory;
    private int numAttempts;
    
    public FlashCard(String question, String answer) {
        this.question = question;
        this.answer = answer;
        rightWrongHistory = new int[100];
        numAttempts = 0;
    }
    
    public String getQuestion() {
        return question;
    }
    
    public String getAnswer() {
        return answer;
    }
    
    public void setQuestion(String newQuestion) {
        question = newQuestion;
    }
    
    public void setAnswer(String newAnswer) {
        answer = newAnswer;
    }
    
    public boolean autoCheckCorrect(String attempt) {
        if (question.equalsIgnoreCase(attempt)){
            rightWrongHistory[numAttempts++] = 1;
            return true;
        } else {
            rightWrongHistory[numAttempts++] = 0;
            return false;
        }
    }
    
    public void userChecksAnswer(boolean correct) {
        if (correct) {
            rightWrongHistory[numAttempts++] = 1;
        } else {
            rightWrongHistory[numAttempts++] = 0;
        }
    }
    
    public double getPercentCorrect() {
        double sum = 0;
        for(int i = 0; i < numAttempts; i++) {
            sum += rightWrongHistory[i];
        }
        
        return sum / numAttempts;
    }
    
    public double getPercentCorrectLast5() {
        double sum = 0;
        for (int i = numAttempts - 5; i < numAttempts; i++) {
            sum += rightWrongHistory[i];
        }
        
        return sum / 5;
    }
}
