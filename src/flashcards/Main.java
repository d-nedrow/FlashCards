package flashcards;

import flashcards.drivers.ProgramDriver;
import java.util.Scanner;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Project's Main class containing main() method for program entry.
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root
                = FXMLLoader.load(getClass().getResource("LoginWindow.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //launch(args); // uncomment to allow GUI to launch
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter 1 for Console Interface, 2 for GUI ");
        String input = reader.next();
        if (input.equals("1")) {
            ProgramDriver.testProgramWithConsoleInterface();
            System.exit(0);
        }
        else {
            launch(args);
        }
        
        // test existing model classes with temporary console interface driver
        //ProgramDriver.testProgramWithConsoleInterface();
        //System.exit(0); // required to exit when there is no GUI exit
    }

}
