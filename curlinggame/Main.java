/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package testopenCV;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

public class Main {
    private VideoCapture capture;
    private GameManager gameEngine;
    private TokenDetector tkd;
    private List<Point> centers;
    private boolean state;
    // Constructeur
    public Main() {
        String libPath = "C:\\opencv\\build\\java\\x64\\opencv_java4100.dll";
        System.load(libPath);
        centers = new ArrayList<Point>();
        capture = new VideoCapture(0);
        state = true;
        if (capture.isOpened()) {
            Mat frame = captureFrame();
            if (!frame.empty()) {
                gameEngine = new GameManager(new Point(200, 200));
                Mat background = frame.clone();
                tkd = new TokenDetector(background, List.of(0, 40));
            } else {
                System.out.println("Error: Unable to initialize background.");
            }
        }
        start();
    }

    public Mat captureFrame() {
        Mat frame = new Mat();
        if (capture.isOpened()) {
            capture.read(frame);
        }
        return frame;
    }
    private Double dist(Point A, Point B){
    	
    	return Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2));
    }
    public boolean isStable(List<Point> rayons, double threshold) {
        if (rayons.size() < 20) {
            return false; 
        }
        
        double sum = 0.0;
        for (int i = rayons.size() - 20; i < rayons.size() - 1; i++) {
            double distance = dist(rayons.get(i), rayons.get(i + 1));
            if (distance > threshold) {
                return false; 
            }
            sum += distance;
        }
        
        double normalizedSum = sum / 20.0;
        return normalizedSum < threshold;
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
            
            Point c = tkd.detectTokenWithCanny(displayFrame);
            if(c != null) {
            	centers.add(c);
            };
            
            
            
            if (isStable(centers, 50) != state ) {
            	state = !state;
            	if (state) {
                	System.out.println("stable");

            	} else {
            		System.out.println("mooved");

            	}
            	
            }
            
            tkd.displayCircles(displayFrame, gameEngine.getCenters(), gameEngine.getRadius(), gameEngine.getTeamid());
            tkd.displayHud(displayFrame, gameEngine.scoreGameBLUE, gameEngine.scoreGameRED, gameEngine.avLive);
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
                        centers.clear();
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
    	Main app = new Main();
        app.start();
    }
}

