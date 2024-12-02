package testopenCV;

import java.util.ArrayList;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;

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
        this.background = grayFrame.clone(); 
    }

    public Mat increaseContrast(Mat frame, double alpha, double beta) {
        Mat result = new Mat();
        frame.convertTo(result, -1, alpha, beta);
        return result;
    }
    
    public Mat detectTokenWithCanny(Mat frame) {
        Mat grayFrame = new Mat();
        Mat edges = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);    

        Core.absdiff(grayFrame, background, grayFrame);

        Imgproc.Canny(grayFrame, edges, 50, 150);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        for (MatOfPoint contour : contours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double contourArea = Imgproc.contourArea(contour);
            if (contourArea > 15) {
                RotatedRect ellipse = Imgproc.fitEllipse(contour2f);
                if (Math.abs(ellipse.size.width - ellipse.size.height) / ellipse.size.width < 0.2) {
                    Imgproc.ellipse(frame, ellipse, new Scalar(0, 255, 0), 3);
                }
            }
        }

        return frame;
    }

    public Mat detectToken(Mat frame) {
        Mat grayFrame = new Mat();
        Mat absDiff = new Mat();
        Mat blurred = new Mat();
        Mat circles = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        Core.absdiff(grayFrame, background, absDiff);

        Imgproc.GaussianBlur(absDiff, blurred, new org.opencv.core.Size(9, 9), 6);
        Mat m = increaseContrast(blurred,10.0, 5.);
        
        Imgproc.HoughCircles(
            m,            
            circles,            
            Imgproc.HOUGH_GRADIENT,
            1.3,                
            30,                 
            150,                
            30,                 
            0,                  
            0                   
        );
 
        if (!circles.empty()) {
        	System.out.println("circles!");
            for (int i = 0; i < circles.cols(); i++) {
                double[] circleData = circles.get(0, i);
                if (circleData != null && circleData.length >= 3) {
                    int x = (int) circleData[0];
                    int y = (int) circleData[1];
                    int radius = (int) circleData[2];

                    Imgproc.circle(frame, new Point(x, y), radius, new Scalar(0, 255, 0), 3);
                    Imgproc.circle(frame, new Point(x, y), 3, new Scalar(0, 0, 255), 3);
                }
            }
        } else {
        }
        return frame;
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
