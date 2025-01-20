package fise2.image4.projetinfo;

import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

public class CameraCapture {
	private VideoCapture capture;

    public CameraCapture() {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME); LIGNE DE BERMAD QUI FONCTIONNE PAS
    	String libPath = System.getProperty("user.dir") + "/libs/opencv_java4100.dll";
        System.load(libPath);
        capture = new VideoCapture(0);  // 0 pour la première webcam
    }

    public Mat captureFrame() {
        Mat frame = new Mat();
        if (capture.isOpened()) {
            capture.read(frame);
        }
        return frame;
    }
    public static void main(String[] args) {
		CameraCapture c = new CameraCapture();

	        if (!c.capture.isOpened()) {
	            System.out.println("Error: Could not open video stream.");
	            return;
	        }

	        Mat frame = c.captureFrame();
	        
	        while (true) {
	            if (c.capture.read(frame)) {
	                HighGui.imshow("Stream", frame);

	                if (HighGui.waitKey(30) == 'q') {
	                    break;
	                }
	            } else {
	                System.out.println("Error: Unable to capture frame.");
	                break;
	            }
	        }

	        c.capture.release();
	        HighGui.destroyAllWindows();
	}
}
