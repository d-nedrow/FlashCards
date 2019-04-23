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
    @FXML private Label cardInfoLabel;
    @FXML private Label flashcardNumLabel;
    @FXML private User user;
    @FXML private ObservableList<String> items;
    @FXML private ArrayList<FlashCard> flashcardList;
    @FXML private String curSubject;
    @FXML private BorderPane flashcardPane;
    
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
        //clearCards(); // will not change the display
        //populateFlashCards();
    }
    /**
     * TODO: Fix these methods to throw errors properly
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
        VBox vbox = new VBox(15);
        vbox.setSpacing(15);
        vbox.setPadding(new Insets(200, 50, 200, 50));
        
        Label question = new Label(card.getQuestion());
        TextField answer = new TextField();
        Button checkBtn = new Button("Check Answer");
        checkBtn.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent event) 
            {
                System.out.println("Validating answer...");
                
                if (card.autoCheckCorrect(answer.getText()))
                {
                    System.out.println("Correct answer!");
                    // TODO: Update card information, cardInfoLabel
                    displayFlashCardInfo(card);
                }
                else
                {
                    System.out.println("Incorrect answer!");
                    displayFlashCardInfo(card);
                }
                // TODO: add handling code
            }
        });
        
        flashcardNumLabel.setText("Flashcard " + (flashcardListPtr + 1) + " of " + flashcardList.size());
        
        displayFlashCardInfo(card);
        
        vbox.getChildren().addAll(question, answer, checkBtn);
        return vbox;
    }
    
    private void displayFlashCardInfo(FlashCard card)
    {
        /*if (card == null) 
        {
            cardInfoLabel.setText("No flashcard in focus.");
        }
        else
        { */
            int nAttempts = card.getNumAttempts();
            int cAttempts = card.getNumCorrect();
            int iAttempts = card.getNumIncorrect();
            cardInfoLabel.setText("Total Attempts: " + nAttempts + "\nCorrect Attempts: " + cAttempts
                    + "\nIncorrect Attempts: " + iAttempts);
        //}
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { // basically a constructor
        System.out.println("The MWC is being initialized...");
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
        System.out.println("MWC done.");
    }  
}
