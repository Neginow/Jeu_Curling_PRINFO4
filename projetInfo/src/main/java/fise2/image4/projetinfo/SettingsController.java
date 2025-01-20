package fise2.image4.projetinfo;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

/**
 *
 * @author KazuWaii
 */
public class SettingsController {
    
    @FXML
    private Spinner nbTurnsSpinner;

    // Shared variables to manage team turns
    private static int nbTeamTurns = 8;

    @FXML
    private void switchToMenu() throws IOException {
        saveSettingsToFile();
        App.setRoot("primary");
    }


    @FXML
    private void initialize() {
        SpinnerValueFactory<Integer> valueFactoryRed = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 8);
        nbTurnsSpinner.setValueFactory(valueFactoryRed);
    }
    
    @FXML
    private void saveSettingsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("settings.txt"))) {
            writer.write("nbTurns=" + nbTurnsSpinner.getValue());
            System.out.println("Settings saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to save settings to file.");
        }
    }

    public static int getNbTeamTurns() {
        return nbTeamTurns;
    }


}
