/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flashcards;

import flashcards.model.FlashCard;
import flashcards.model.Subject;
import flashcards.model.User;
import java.awt.Color;
import javafx.geometry.Insets;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 * Main Window Controller Class
 *
 * @author Conner Brigman
 */
public class MainWindowController implements Initializable {
    
    
    @FXML private ListView<String> listView;
    @FXML private Label welcomeLabel;
    @FXML private User user;
    @FXML private ObservableList<String> items;
    @FXML private String curSubject;
    @FXML private GridPane flashcardPane;

    @FXML
    private void createNewSubjectBtn(ActionEvent event)
    {
        System.out.println("Creating new subject..."); // placeholder
        
        TextInputDialog prompt = new TextInputDialog("Enter Subject");
        prompt.setTitle("Create new Subject");
        prompt.setContentText("Enter Subject Name:");
        
        Optional<String> result = prompt.showAndWait();
        if (result.isPresent()) {
            System.out.println("Subject: " + result.get());
            Subject subject = new Subject(result.get());
            user.addSubject(subject); // done
        }
        else {
            System.out.println("No input detected."); // done
        }
        
        user.saveUserState();
        populateListView();
    }
    
    @FXML
    private void addFlashCardBtn(ActionEvent event) // BUG - if more than 2x3, adds to wrong subject
    {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Prompt");
        dialog.setHeaderText("Enter Flashcard Info");

        ButtonType addBtn = new ButtonType("Add", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(150, 40, 30, 20));

        TextField question = new TextField();
        TextField answer = new TextField();

        grid.add(new Label("Question:"), 0, 0);
        grid.add(question, 1, 0);
        grid.add(new Label("Answer:"), 0, 1);
        grid.add(answer, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        Platform.runLater(() -> question.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addBtn) {
                return new Pair<>(question.getText(), answer.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(questionAnswer -> {
            System.out.println("Question=" + questionAnswer.getKey() + ", Answer=" + questionAnswer.getValue());
            
            //ArrayList<String> subjects = new ArrayList(); // loop through list
            for (Subject subj : user.getSubjects()) {
                if (subj.getTitle().equals(curSubject)) { // find corresponding subject object
                    subj.addFlashCard(questionAnswer.getKey(), questionAnswer.getValue()); // add flash card to selected subject
                }
            }
        });
        
        user.saveUserState(); // add flash card to file
        clearCards();
        populateFlashCards();
    }
    
    @FXML
    private void refreshListBtn(ActionEvent event)
    {
        populateListView();
    }
    
    @FXML
    /**
     * Sets the user to reference for loading FlashCards.
     * @param user User object
     */
    public void setUser(User user)
    {
        this.user = user;
        welcomeLabel.setText("Welcome, " + user.getUsername()); // done
        populateListView();
    }
    
    private void populateListView()
    {
        System.out.println("Starting population...");
        
        ArrayList<String> subjects = new ArrayList();
        for (Subject subj : user.getSubjects()) {
            subjects.add(subj.getTitle());
        }
        
        items = FXCollections.observableArrayList(subjects);
        listView.setItems(items);
        
        System.out.println("Done.");
        
        /* (items.isEmpty() || items == null) {
            listView.setPlaceholder(new Label("No subjects added."));
        } */
    }
    
    private void populateFlashCards()
    {
        //int col = 0;
        int row = 0;
        //int count = 0;
        
        ArrayList<FlashCard> cardList = new ArrayList<>();
        ArrayList<Subject> subjList = user.getSubjects();
        //Subject subj = new Subject(curSubject);        
        GridPane[] gridArr = new GridPane[20];
        
        final int MAXCOL = 2;
        final int MAXROW = 3;
        
        for (Subject subj : subjList) // find current subject, inefficient
        { 
            if (subj.getTitle().equals(curSubject)) // find corresponding subject object
            { 
                cardList = subj.getFlashCards();
                if (cardList.isEmpty()) 
                {
                    break;
                }
                else 
                {
                    int count = 0;

                    for (FlashCard card : cardList) 
                    {
                        gridArr[count] = buildFlashCard(card);
                        count++;
                    }
                    int pointer = 0;
                    for (int col = 0; col <= MAXCOL; col++) {
                        if (gridArr[pointer] == null) {
                            break;
                        }
                        else if (col == MAXCOL) { // if bounds reached
                            col = 0;
                            row++;
                            flashcardPane.add(gridArr[pointer], col, row); 
                        } else {
                            flashcardPane.add(gridArr[pointer], col, row); //throws NullPointer, fixable, check conditions
                        }
                        pointer++;
                    }
                    break;
                }
            }
            else 
            {
                System.out.println("Continuing search...");
            }
            
            //for (int i = 0; i < subj.getNumFlashcards(); i++) { // outer loop, NTE this amount
                
            //break; // subject found, no need to revisit loop
        }
        //System.out.println("CardList" + cardList.toString());    
        /*for (int i = 0; i <= MAXCOL; i++) {
            if (i == MAXCOL) {
                i = 0; // reset col count
                row++;
                flashcardPane.add(grid, i, row);
            } else {
                flashcardPane.add(grid, i, row);
            }
        } */
    }
    
    private void clearCards()
    {
        flashcardPane.getChildren().clear();
        //flashcardPane.setGridLinesVisible(true); // nope
    }
    
    // Helper method
    private GridPane buildFlashCard(FlashCard card) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        
        //grid.setStyle("-fx-background-color:#ffffff; -fx-border-style"); // later

        TextField answer = new TextField();
        Button checkBtn = new Button("Check Answer"); // handling code here

        grid.add(new Label(card.getQuestion()), 0, 0);
        grid.add(new Label("Answer:"), 1, 0);
        grid.add(answer, 2, 0);
        grid.add(checkBtn, 0, 1);
        return grid;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { // basically a constructor
        System.out.println("The MWC is being initialized...");
        listView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov,
                    String oldSel, String newSel) 
            {
                System.out.println("Selection: " + newSel); // on selection
                curSubject = newSel;
                // TODO: clear flashcard display
                clearCards();
                populateFlashCards();
            }
        });
        System.out.println("MWC done.");
    }  
}
