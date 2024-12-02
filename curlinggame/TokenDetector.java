package testopenCV;

import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

public class TokenDetector {
    
    private Mat background;  // background in grayscale
    private List<Integer> range;  // search range for HoughCircles

    public TokenDetector(Mat background, List<Integer> range) {
    	setBackground(background);
    	this.range = range;
    }

    // Set a new background (useful if it needs to be updated during runtime)
    public void setBackground(Mat background) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(background, grayFrame, Imgproc.COLOR_BGR2GRAY);
        this.background = grayFrame;
    }

    // Detect a token in the given frame (returns a Circle object)
    public Circle detectToken(Mat frame) {
        Mat grayFrame = new Mat();
        Mat absDiff = new Mat();
        Mat circles = new Mat();
        
        // Convert the frame to grayscale
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        
        // Compute absolute difference with the background
        Core.absdiff(grayFrame, background, absDiff);

        // Detect circles using HoughCircles
        Imgproc.HoughCircles(
        		absDiff,
                circles,
                Imgproc.HOUGH_GRADIENT,
                1,                          // Resolution
                3,                          // Minimum distance between circle centers
                100,                        // Canny edge detector threshold
                50,                         // Threshold for center detection
                this.range.get(0),          // Min radius
                this.range.get(1)           // Max radius
        );

        // Check if any circles are detected
        if (circles.empty()) {
            //System.out.println("No circles detected.");
            return null;  // No circle detected
        }

        // Retrieve the first circle's data (assuming only one circle is detected)
        double[] coords = circles.get(0, 0);
        if (coords == null || coords.length < 3) {
            throw new IllegalStateException("Invalid circle data detected!");
        }

        Point center = new Point(coords[0], coords[1]); 
        int radius = (int) coords[2];

        // Return the detected circle
        return new Circle(center, radius);
    }

    // Display detected circles (for visualization)
    public Mat displayCircles(Mat frame, List<Point> centers, List<Integer> radius, List<Boolean> teamid) {
    	if (centers.size() == radius.size() && radius.size() == teamid.size()) {
    	    for (int i = 0; i < centers.size(); i++) {
    	        int color1 = 255 * (teamid.get(i) ? 1 : 0);
    	        int color2 = 255 * (!teamid.get(i) ? 1 : 0);
    	        Imgproc.circle(frame, centers.get(i), radius.get(i), new Scalar(color1, 0, color2), -1);
    	     
    	    }
    	} else {
    	    System.err.println("Mismatched input sizes for displayCircles!");
    	}
    	return frame;
    }
}
