package testopenCV;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Could not open video stream.");
            return;
        }

        GameManager gameEngine = new GameManager(new Point(200,200));
        Mat frame = new Mat();
        capture.read(frame);

        Mat background = frame.clone();
        TokenDetector tkd = new TokenDetector(background, List.of(0, 40));

        int key = -1;

        while (key != 'Q') {

            if (!capture.read(frame)) {
                System.out.println("Error: Could not read frame from stream.");
                break;
            }

            Mat displayFrame = frame.clone(); // par pitie ne pas enlever aucun clone car opencv java a des EFFETS DE BORD PARTOUT !!!! (sinon ca va planter ou pire bugger)
            tkd.displayCircles(displayFrame, gameEngine.getCenters(), gameEngine.getRadius(), gameEngine.getTeamid());
            tkd.displayHud(displayFrame, gameEngine.scoreGameBLUE, gameEngine.scoreGameRED);
            tkd.displayTarget(displayFrame, gameEngine.getTarget());
            
            HighGui.imshow("Webcam Stream", displayFrame);

            key = HighGui.waitKey(10);

            if (key != -1) {
                char pressedKey = (char) key;
                System.out.println("Key pressed: " + pressedKey);

                if (pressedKey == 'F' || pressedKey == 'f') {
                    tkd.setBackground(frame.clone()); 
                }

                if (pressedKey == 'T' || pressedKey == 't') {
                    Circle circle = tkd.detectToken(frame);
                    if (circle != null) {
                        gameEngine.addCircle(circle.getCenter(), circle.getRadius());
                        
                    }
                }
            }
            gameEngine.update();
        }

        capture.release();
        HighGui.destroyAllWindows();
    }
}

