/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flashcards;

import static flashcards.drivers.ProgramDriver.generateSecurePassword;
import static flashcards.drivers.ProgramDriver.getSalt;
import flashcards.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Daniel Nedrow, Conner Brigman
 */
public class LoginWindowController implements Initializable {
    
    
    @FXML private TextField userField, passField;
    @FXML private String username, password;
    
    @FXML
    private void handleLogin(ActionEvent event) {
        System.out.println("Handling login..."); // placeholder
        
        username = userField.getText();
        password = passField.getText();
        
        if (User.login(username, password) == null) { // if account is not found
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setContentText("This account does not exist. Would you like to create it?");
            
            ButtonType yesBtn = new ButtonType("Yes");
            ButtonType cancelBtn = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
            
            alert.getButtonTypes().setAll(yesBtn, cancelBtn);
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get()== yesBtn) { // register a new user account
                
                handleRegistration(event);
                
                /*boolean isDuplicate = User.isDuplicateUser(username); // check if duplicate
                if (isDuplicate) {
                    System.out.println("An account with this username already exists.");
                    // do something
                }
                else
                {
                    User newUser = new User(username, password);                    
                    newUser.saveUsernameAndPassword(); // add to users.txt
                    
                    User userState = User.login(username, password); // contains flash cards, subjects, etc.
                    
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("MainWindow.fxml"));
                    try {
                        loader.load();
                    } catch (IOException ex) {
                        System.out.println("Oops, something went wrong.");
                    }
                    MainWindowController mwc = loader.getController();
                    mwc.setUser(userState); // pass user object into Main Window

                    Parent P = loader.getRoot();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(P));
                    stage.showAndWait();
                } */
                
            }
            //else if (result.get() == noBtn) { // closes the window
            //    alert.close();
            //}
            else { // cancel
                alert.close();
            }
        }
        else { // if account is found, normal login     
            
            User user = User.login(username, password);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            
            try {
                Stage stage = new Stage(); 
                Parent root = (Parent) fxmlLoader.load();
                stage.setTitle("Flashcards");
                stage.setScene(new Scene(root));
                
                MainWindowController mwc = fxmlLoader.<MainWindowController>getController();
                mwc.setUser(user);
                
                stage.show();
                // hide Login Window
                
            } catch (IOException ex) {
                Logger.getLogger(LoginWindowController.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("something went wrong");
            }
        }
    }
    
    @FXML
    private void handleRegistration(ActionEvent event)
    {
        System.out.println("Handling registration..."); // placeholder

        username = userField.getText();
        password = passField.getText();
        
        boolean isDuplicate = User.isDuplicateUser(username);
        if (isDuplicate) {
            System.out.println("An account with this username already exists.");
            // additional handling here?
        }
        else {
            User newUser = new User(username, password);
            
            // I added this code from Sabrina to make registering users on the GUI compatible
            // with the new password encryption functionality. (D. Nedrow)
            byte[] salt = getSalt();
            String encryptedPassword = generateSecurePassword(password, salt.toString());
            newUser.setSalt(salt.toString());
            newUser.setEncryptedPassword(encryptedPassword);
            
            newUser.saveUsernameAndPassword(); // add to users.txt
            newUser.saveUserState(); // populate user information
            
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Successful");
            alert.setContentText("User account created successfully.");

            alert.showAndWait();
            
            // ---- END REGISTRATION ----
            
            //newUser = User.login(username, password);

            //User userState = User.login(username, password); // finally login
            
            /*FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainWindow.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                System.out.println("Oops, something went wrong.");
            }
            MainWindowController mwc = loader.getController();
            mwc.setUser(newUser); // pass user object into Main Window
            
            Parent P = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(P));
            stage.showAndWait(); */
        }
    }
    // For later
    /*private void openMainWindow(User newUser)
    {
        FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("MainWindow.fxml"));
            try {
                loader.load();
            } catch (IOException ex) {
                System.out.println("Oops, something went wrong.");
            }
            MainWindowController mwc = loader.getController();
            mwc.setUser(newUser); // pass user object into Main Window
            
            Parent P = loader.getRoot();
            Stage stage = new Stage();
            stage.setScene(new Scene(P));
            stage.showAndWait();
    } */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) { // basically a constructor
        // TODO
    }
    
}
