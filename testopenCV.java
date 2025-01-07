package testopenCV;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.highgui.HighGui;

public class testopenCV {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture capture = new VideoCapture(0);
        if (!capture.isOpened()) {
            System.out.println("Erreur : Impossible d'ouvrir la caméra");
            return;
        }
        Mat frame = new Mat();
        while (true) {
            if (capture.read(frame)) {
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
                
                HighGui.imshow("test", frame);
                if (HighGui.waitKey(1) == 'q') {
                    break;
                }
            } else {
                System.out.println("Erreur : Impossible de lire l'image de la caméra");
                break;
            }
        }

        capture.release();
        HighGui.destroyAllWindows();
    }
}
