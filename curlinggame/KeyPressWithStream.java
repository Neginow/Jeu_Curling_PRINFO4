package testopenCV;

import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

public class Main {
    static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    public static void main(String[] args) {
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Could not open video stream.");
            return;
        }

        Mat frame = new Mat();
        capture.read(frame);

        Mat background = frame;
        TokenDetector tkd = new TokenDetector(background, List.of(0, 40));  

        int key = -1;

        while (key != 'Q') {
            HighGui.imshow("Webcam Stream", frame);

            if (!capture.read(frame)) {
                System.out.println("Error: Could not read frame from stream.");
                break;
            }
            //frame = tkd.detectToken(frame);

            key = HighGui.waitKey(10);
            if (key != -1) {
                char pressedKey = (char) key;
                System.out.println("Key: " + pressedKey);

                if (pressedKey == 'F' || pressedKey == 'f') {
                    tkd.setBackground(frame);
                }
                if (pressedKey == 'T' || pressedKey == 't') {
                    frame = tkd.detectToken(frame);
                }
                
            }
        }
    }

}

