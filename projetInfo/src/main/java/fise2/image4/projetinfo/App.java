package fise2.image4.projetinfo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Classe principale de l'application JavaFX.
 */
public class App extends Application {

    // La scène principale de l'application.
    private static Scene scene;

    // Dimensions par défaut de la fenêtre.
    private static final int width = 1125;
    private static final int height = 625;

    /**
     * Méthode appelée au lancement de l'application JavaFX.
     * @param stage La fenêtre principale (Stage) de l'application.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Création de la scène avec une StackPane comme racine et des dimensions fixes.
        scene = new Scene(new StackPane(), width, height);

        // Ajout de la feuille de style CSS à la scène.
        scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());

        // Définir le contenu initial de la scène.
        setRoot("primary");

        // Empêcher la redimensionnement de la fenêtre par l'utilisateur.
        stage.setResizable(false);

        // Affichage de la fenêtre.
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Définit le contenu de la racine de la scène en chargeant un fichier FXML et en ajoutant un fond d'écran.
     * @param fxml Nom du fichier FXML (sans extension) à charger comme contenu principal.
     * @throws IOException Si une erreur survient lors du chargement du fichier FXML.
     */
    static void setRoot(String fxml) throws IOException {
        // Charger le contenu FXML.
        Parent parent = loadFXML(fxml);

        // Charger l'image de fond d'écran.
        Image image = new Image(App.class.getResource("background.png").toString());

        // Créer une ImageView pour afficher l'image en arrière-plan.
        ImageView background = new ImageView(image);

        // Lier les dimensions de l'image à celles de la scène pour qu'elle remplisse la fenêtre.
        background.fitWidthProperty().bind(scene.widthProperty());
        background.fitHeightProperty().bind(scene.heightProperty());

        // Désactiver la préservation du ratio pour que l'image s'adapte parfaitement.
        background.setPreserveRatio(false);

        // Créer une nouvelle StackPane pour contenir le fond d'écran et le contenu principal.
        StackPane root = new StackPane();
        root.getChildren().addAll(background, parent);

        // Définir la nouvelle racine de la scène.
        scene.setRoot(root);
    }

    /**
     * Charge un fichier FXML depuis les ressources.
     * @param fxml Nom du fichier FXML (sans extension) à charger.
     * @return Le nœud racine du fichier FXML chargé.
     * @throws IOException Si une erreur survient lors du chargement.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        // Créer un FXMLLoader pour charger le fichier FXML depuis les ressources.
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Méthode principale de l'application. Lance l'application JavaFX.
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        launch(args); // Démarre l'application JavaFX.
    }

}
