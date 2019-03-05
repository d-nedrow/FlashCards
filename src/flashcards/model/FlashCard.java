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
    
    public int getNumAttempts() {
        return numAttempts;
    }
    
    public int[] getRightWrongHistory() {
        return rightWrongHistory;
    }
    
    public void setRightWrongHistory(String numbers) {
        String[] history = numbers.split(" ");
        
        for (int i = 0; i < history.length; i++) {
            rightWrongHistory[i] = Integer.parseInt(history[i]);
            numAttempts++;
        }
    }
    
    public boolean autoCheckCorrect(String attempt) {
        if (answer.equalsIgnoreCase(attempt)){
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
        int sum = 0;
                
        for(int i = 0; i < numAttempts; i++) {
            sum += rightWrongHistory[i];
        }
        
        return (sum * 1.0 / numAttempts) * 100;
         
    }
    
    public double getPercentCorrectLast5() {
        int sum = 0;
        int divisor = Math.min(numAttempts, 5);
        
        int i = Math.max(numAttempts - 5, 0);
        for (; i < numAttempts; i++) {
            sum += rightWrongHistory[i];
        }
        
        return (sum * 1.0 / divisor) * 100;
    }
}
