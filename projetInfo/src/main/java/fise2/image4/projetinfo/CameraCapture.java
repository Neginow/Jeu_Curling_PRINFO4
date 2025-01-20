package fise2.image4.projetinfo;

import org.opencv.core.Mat; // Importation de la classe Mat pour manipuler des images.
import org.opencv.highgui.HighGui; // Importation pour afficher des fenêtres avec OpenCV.
import org.opencv.videoio.VideoCapture; // Importation pour capturer des flux vidéo depuis une webcam ou une vidéo.

public class CameraCapture {
	private VideoCapture capture; // Instance de VideoCapture pour accéder au flux de la webcam.

    /**
     * Constructeur de la classe CameraCapture.
     * Initialise la bibliothèque OpenCV et ouvre la webcam.
     */
    public CameraCapture() {

        // Chargement manuel de la bibliothèque OpenCV depuis le chemin local.
    	String libPath = System.getProperty("user.dir") + "/libs/opencv_java4100.dll";
        System.load(libPath);

        // Initialisation de la capture vidéo avec l'index 0 (première webcam détectée).
        capture = new VideoCapture(0);
    }

    /**
     * Capture une image (frame) depuis le flux vidéo.
     * @return Un objet Mat contenant l'image capturée. Si aucune image n'est capturée, retourne une Mat vide.
     */
    public Mat captureFrame() {
        Mat frame = new Mat(); // Création d'un objet Mat pour stocker l'image capturée.
        if (capture.isOpened()) { // Vérifie si la webcam est correctement ouverte.
            capture.read(frame); // Lecture d'une image depuis le flux vidéo et stockage dans "frame".
        }
        return frame; // Retourne l'image capturée.
    }

    /**
     * Méthode principale. Lance la capture vidéo et affiche le flux dans une fenêtre.
     * @param args Arguments de la ligne de commande (non utilisés ici).
     */
    public static void main(String[] args) {
        // Création d'une instance de la classe CameraCapture.
		CameraCapture c = new CameraCapture();

        // Vérifie si la capture vidéo a été correctement ouverte.
        if (!c.capture.isOpened()) {
            System.out.println("Error: Could not open video stream."); // Affiche une erreur si la webcam n'est pas accessible.
            return; // Termine le programme si la capture échoue.
        }

        // Capture une première image depuis la webcam.
        Mat frame = c.captureFrame();

        // Boucle principale pour afficher le flux vidéo.
        while (true) {
            if (c.capture.read(frame)) { // Lit une image depuis le flux vidéo.
                HighGui.imshow("Stream", frame); // Affiche l'image capturée dans une fenêtre nommée "Stream".

                // Attend 30 ms pour une touche et sort si 'q' est pressée.
                if (HighGui.waitKey(30) == 'q') {
                    break; // Quitte la boucle si l'utilisateur appuie sur 'q'.
                }
            } else {
                // Affiche une erreur si aucune image n'a pu être capturée.
                System.out.println("Error: Unable to capture frame.");
                break; // Quitte la boucle en cas d'erreur.
            }
        }

        // Libère les ressources utilisées par la capture vidéo.
        c.capture.release();

        // Ferme toutes les fenêtres ouvertes par HighGui.
        HighGui.destroyAllWindows();
    }
}
