/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flashcards;

import flashcards.model.Subject;
import flashcards.model.User;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

/**
 * FXML Controller class
 *
 * @author Conner
 */
public class MainWindowController implements Initializable {
    
    @FXML
    private Label welcomeLabel;
    private ListView<Subject> listView;
    private User user;
    private ObservableList<Subject> items;

    @FXML
    private void createNewSubject(ActionEvent event)
    {
        System.out.println("Creating new subject..."); // placeholder
        
        TextInputDialog prompt = new TextInputDialog("walter");
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
        
        //user.saveUserState(); causing duplicates
    }
    
    @FXML
    private void refreshList(ActionEvent event)
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
        user.loadUserState();
        welcomeLabel.setText("Welcome, " + user.getUsername()); // done
        
        //populateListView();
    }
    
    private void populateListView()
    {
        System.out.println("Starting population...");
        items = FXCollections.observableArrayList(new Subject("Test Subject"));
        listView = new ListView<>(items);
        listView.setItems(items);
        
        /* (items.isEmpty() || items == null) {
            listView.setPlaceholder(new Label("No subjects added."));
        } */
        //listView = new ListView<>();
        
        //items = FXCollections.observableArrayList(new Subject("Test Subject"));
        //listView.setItems(items);
        //System.out.println(items.toString());
        
        
        //items = FXCollections.observableList(user.getSubjects()); // gets arraylist of subjects, turns into observable list
        //listView.setItems(items);
        
        //System.out.println(listView.toString()); // temp
        
        
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { // basically a constructor
        System.out.println("The MWC is being initialized...");
        System.out.println("MWC done.");
    }  
}
