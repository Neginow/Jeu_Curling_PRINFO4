/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetinfo.projetinfo;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

public class ProjetInfo {
    private VideoCapture capture;
    private GameManager gameEngine;
    private TokenDetectorbis tkd;
    
    // Constructeur
    public ProjetInfo() {
        String libPath = System.getProperty("user.dir") + "/libs/opencv_java4100.dll";
        System.load(libPath);

        capture = new VideoCapture(0);

        if (capture.isOpened()) {
            Mat frame = captureFrame();
            if (!frame.empty()) {
                gameEngine = new GameManager(new Point(200, 200));
                Mat background = frame.clone();
                tkd = new TokenDetectorbis(background, List.of(0, 40));
            } else {
                System.out.println("Error: Unable to initialize background.");
            }
        }
    }

    // Prise d'une photo
    public Mat captureFrame() {
        Mat frame = new Mat();
        if (capture.isOpened()) {
            capture.read(frame);
        }
        return frame;
    }

    // Gestion du jeu
    public void start() {
        // Erreur si la caméra n'est pas trouvée
        if (!capture.isOpened()) {
            System.out.println("Error: Could not open video stream.");
            return;
        }

        Mat frame = new Mat();
        int key = -1;

        while (true) {
            // Erreur si la camera n'est plus lisible
            if (!capture.read(frame)) {
                System.out.println("Error: Could not read frame from stream.");
                break;
            }

            // Création de la frame à afficher
            Mat displayFrame = frame.clone();
            tkd.displayCircles(displayFrame, gameEngine.getCenters(), gameEngine.getRadius(), gameEngine.getTeamid());
            tkd.displayHud(displayFrame, gameEngine.scoreGameBLUE, gameEngine.scoreGameRED);
            tkd.displayTarget(displayFrame, gameEngine.getTarget());

            // Affichage
            HighGui.imshow("Webcam Stream", displayFrame);

            key = HighGui.waitKey(10);

            // Gestion du jeu par les touches
            if (key != -1) {
                char pressedKey = (char) key;
                System.out.println("Key pressed: " + pressedKey);

                if (pressedKey == 'F' || pressedKey == 'f') {
                    tkd.setBackground(frame.clone());
                }
                
                if (pressedKey == 'C' || pressedKey == 'c') {
                    tkd.fixRadius(frame.clone());
                }

                if (pressedKey == 'T' || pressedKey == 't') {
                    Circle circle = tkd.detectToken(frame);
                    if (circle != null) {
                        gameEngine.addCircle(circle.getCenter(), circle.getRadius());
                        gameEngine.winnerLive() ;
                    }
                }

                if (pressedKey == 'Q' || pressedKey == 'q') {
                    break;
                }
            }

            // Tour
            gameEngine.update();
        }

        releaseResources();
    }

    // Quitte le jeu
    private void releaseResources() {
        capture.release();
        HighGui.destroyAllWindows();
    }

    // main
    public static void main(String[] args) {
    	ProjetInfo app = new ProjetInfo();
        app.start();
    }
}

