/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flashcards;

import flashcards.model.FlashCard;
import flashcards.model.Subject;
import flashcards.model.User;
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
    private void addFlashCardBtn(ActionEvent event)
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
        
        grid.minHeight(640);
        grid.minWidth(480);
        
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
            
            ArrayList<String> subjects = new ArrayList(); // loop through list
            for (Subject subj : user.getSubjects()) {
                if (subj.getTitle().equals(curSubject)) { // find corresponding subject object
                    subj.addFlashCard(questionAnswer.getKey(), questionAnswer.getValue()); // add flash card to selected subject
                }
            }
        });
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
        ArrayList<String> subjects = new ArrayList(); // go through list
        for (Subject subj : user.getSubjects()) { // find current subject
            if (subj.getTitle().equals(curSubject)) { // find corresponding subject object
                for (FlashCard card : subj.getFlashCards()) { // for each flashcard in the subject
                    GridPane grid = new GridPane(); // inner GridPane
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(10, 10, 10, 10));
                    
                    Label question = new Label();
                    TextField answer = new TextField();

                    grid.add(new Label(), 0, 0); // ****START HERE****
                }
            }
        }
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
                populateFlashCards();
            }
        });
        System.out.println("MWC done.");
    }  
}
