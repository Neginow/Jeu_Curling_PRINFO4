package testopenCV;

import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

public class TokenDetector {
    
    private Mat background;
    private List<Integer> range;

    public TokenDetector(Mat background, List<Integer> range) {
    	setBackground(background);
    	this.range = range;
    }

    public void setBackground(Mat background) {
        Mat grayFrame = new Mat();
        Imgproc.cvtColor(background, grayFrame, Imgproc.COLOR_BGR2GRAY);
        this.background = grayFrame;
    }

    public Circle detectToken(Mat frame) {
        Mat grayFrame = new Mat();
        Mat absDiff = new Mat();
        Mat circles = new Mat();
        
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);    
        Core.absdiff(grayFrame, background, absDiff);

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

        if (circles.empty()) {
            //System.out.println("No circles detected.");
            return null;  
        }

        double[] coords = circles.get(0, 0);
        if (coords == null || coords.length < 3) {
            throw new IllegalStateException("Invalid circle data detected!");
        }

        Point center = new Point(coords[0], coords[1]); 
        int radius = (int) coords[2];

        return new Circle(center, radius);
    }

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
