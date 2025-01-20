package fise2.image4.projetinfo;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
    @FXML
    private void switchToSettings() throws IOException {
        App.setRoot("settings");
    }
}
