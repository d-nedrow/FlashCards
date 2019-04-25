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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * Main Window Controller Class
 *
 * @author Conner Brigman
 */
public class MainWindowController implements Initializable {
    
    
    @FXML private ListView<String> listView;
    @FXML private Label welcomeLabel;
    @FXML private Label totAttemptLabel, corAttemptLabel, incAttemptLabel;
    @FXML private Label flashcardNumLabel;
    @FXML private User user;
    @FXML private ObservableList<String> items;
    @FXML private ArrayList<FlashCard> flashcardList;
    @FXML private String curSubject;
    @FXML private BorderPane flashcardPane;
    @FXML private VBox flashcardDisplayBox;
    
    private int flashcardListPtr = 0;

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
    private void removeSubjectBtn(ActionEvent event)
    {
        for(Subject subj: user.getSubjects())
        {
            if (subj.getTitle().equals(curSubject)) 
            {
                user.deleteSubject(subj);
                break;
            }
        }
        
        user.saveUserState();
        populateListView();
    }
    
    @FXML
    private void resetFlashcardsBtn(ActionEvent event)
    {
        for(Subject subj: user.getSubjects())
        {
            subj.resetFlashcards();
        }
        user.saveUserState();
        displayCards();
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
        grid.setPadding(new Insets(40, 40, 30, 20));

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
            
            for (Subject subj : user.getSubjects()) {
                if (subj.getTitle().equals(curSubject)) { // find corresponding subject object
                    subj.addFlashCard(questionAnswer.getKey(), questionAnswer.getValue()); // add flash card to selected subject
                }
            }
        });
        
        user.saveUserState(); // add flash card to file
        displayCards();
    }
    /**
     * Removes a flashcard from the list.
     * @param event 
     */
    // TODO: fix exception
    @FXML
    private void removeFlashcardBtn(ActionEvent event)
    {
        if (!flashcardList.isEmpty()) 
        {
            for (Subject subj : user.getSubjects()) 
            {
                if (subj.getTitle().equals(curSubject)) 
                {
                    subj.removeFlashcard(flashcardList.get(flashcardListPtr));
                    break;
                }
            }
            user.saveUserState();
            displayCards();
        }
        else
        {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Sorry, something went wrong.");
            alert.setContentText("The list is empty - no flashcards can be removed.");

            alert.showAndWait();
        }
    }
    /**
     * Cycles to the next card in the list.
     * @param event 
     */
    @FXML
    private void nextCardBtn(ActionEvent event)
    {
        flashcardListPtr++;
        displayCards();
    }
    
    @FXML
    private void prevCardBtn(ActionEvent event) 
    {
        flashcardListPtr--;
        displayCards();
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
        ArrayList<String> subjects = new ArrayList();
        for (Subject subj : user.getSubjects()) {
            subjects.add(subj.getTitle());
        }
        
        items = FXCollections.observableArrayList(subjects);
        listView.setItems(items);

         if (items.isEmpty() || items == null) {
            listView.setPlaceholder(new Label("No subjects added."));
        }
    }
    
    private void populateFlashCards() // new method
    {
        flashcardList = new ArrayList<>();
        ArrayList<Subject> subjList = user.getSubjects();
        
        for (Subject subj : subjList) 
        {
            if (subj.getTitle().equals(curSubject)) 
            {
                flashcardList = subj.getFlashCards();
                break;
            }
        }
    }
    private void displayCards()
    {
        if (!flashcardList.isEmpty()) 
        {
            if (flashcardListPtr <= 0) // using the prev button, prevents out of bounds
            {
                flashcardListPtr = 0;
                flashcardPane.setCenter(buildFlashCard(flashcardList.get(flashcardListPtr)));
            }
            else if (flashcardListPtr >= flashcardList.size()) 
            {
                flashcardListPtr = flashcardList.size() - 1;
                flashcardPane.setCenter(buildFlashCard(flashcardList.get(flashcardListPtr)));
            }
            else
            {
                flashcardPane.setCenter(buildFlashCard(flashcardList.get(flashcardListPtr))); // gets the first flashcard in the list, use ptr to remember?
            }
            
        }
        else
        {
            flashcardPane.setCenter(new Label("No flashcards in the list!"));
        }
    }
    
    private void clearCards()
    {
        flashcardPane.setCenter(null);
    }    
    
    // Helper method
    private VBox buildFlashCard(FlashCard card) {
        flashcardDisplayBox = new VBox(15); // spacing between children
        flashcardDisplayBox.setPadding(new Insets(100, 50, 180, 50));
        flashcardDisplayBox.setAlignment(Pos.CENTER);
        
        Label question = new Label(card.getQuestion());
        TextField answer = new TextField();
        Button checkBtn = new Button("Check Answer");
        checkBtn.setOnAction((ActionEvent event) -> 
        {
            System.out.println("Validating answer...");
            
            if (card.autoCheckCorrect(answer.getText())) 
            {
                System.out.println("Correct answer!");
                displayFlashCardInfo(card);
            }
            else
            {
                System.out.println("Incorrect answer!");
                displayFlashCardInfo(card);
            }
        });
        
        flashcardNumLabel.setText("Flashcard " + (flashcardListPtr + 1) + " of " + flashcardList.size());
        
        displayFlashCardInfo(card);
        VBox.setMargin(question, new Insets(0, 0, 70, 0));
        flashcardDisplayBox.getChildren().addAll(question, answer, checkBtn);
        user.saveUserState();
        
        return flashcardDisplayBox;
    }
    
    private void displayFlashCardInfo(FlashCard card)
    {
        totAttemptLabel.setText("Total Attempts: " + card.getNumAttempts());
        corAttemptLabel.setText("Correct Attempts: " + card.getNumCorrect());
        incAttemptLabel.setText("Incorrect Attempts: " + card.getNumIncorrect());
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) 
    {
        listView.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() 
                {
                    public void changed(ObservableValue<? extends String> ov,
                            String oldSel, String newSel) 
                        {
                            // *** Program Flow *** TODO: change method names
                            curSubject = newSel; // User selects a subject from the list, selection is set
                            flashcardListPtr = 0; // reset pointer
                            clearCards(); // Clear card already displayed, if any
                            populateFlashCards(); // Populate flash card list from chosen subject above
                            displayCards(); // Display first card of the new list
                            //displayFlashCardInfo(null);
                        }
                });
    }
}
