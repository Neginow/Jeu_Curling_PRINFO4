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
import org.opencv.imgproc.CLAHE;
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

	public Circle detectToken(Mat frame) {
	    Mat grayFrame = new Mat();
	    Mat blackHat = new Mat();
	    Mat blurred = new Mat();
	    Mat background = new Mat();
	    Mat circles = new Mat();
	    Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new org.opencv.core.Size(10,10)); 

	    Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

	    Imgproc.morphologyEx(grayFrame, blackHat, Imgproc.MORPH_BLACKHAT, morphKernel);
	    Core.subtract(grayFrame, blackHat, grayFrame);

	    Imgproc.GaussianBlur(grayFrame, blurred, new org.opencv.core.Size(7, 7), 1.5, 1.5);

	    // Param Explanation:
	    // dp = 1.5 : Inverse ratio of resolution
	    // minDist = 30 : Min distance between circle centers
	    // param1 = 180 : Higher threshold for edge detection (Canny)
	    // param2 = 0.9 : Threshold for center detection (sensitivity)
	    // minRadius = 10, maxRadius = 100 : Detect coins within size limits
	    Imgproc.HoughCircles(blurred, circles, Imgproc.HOUGH_GRADIENT, 1.5, 30, 100, 0.9, 15, 30);

	    if (!circles.empty()) {
	        System.out.println("Detected circles!");
	        double[] circleData = circles.get(0, 0);
	        if (circleData != null && circleData.length >= 3) {
	            int x = (int) circleData[0];
	            int y = (int) circleData[1];
	            int radius = (int) circleData[2];

	            // 8️⃣ **Draw detected circle on the frame**
	            
	            return new Circle(new Point(x, y), radius);
	        }
	    }


	    
	    return null;
	}

	public Mat displayCircles(Mat frame, List<Point> centers, List<Integer> radius, List<Boolean> teamid) {
		if (centers.size() == radius.size() && radius.size() == teamid.size()) {
			for (int i = 0; i < centers.size(); i++) {
				int color1 = 255 * (teamid.get(i) ? 1 : 0);
				int color2 = 255 * (!teamid.get(i) ? 1 : 0);
				Imgproc.circle(frame, centers.get(i), radius.get(i), new Scalar(color1, 0, color2), -1);
			}
		} else {
			System.err.println("il y a un probleme au niveau des tableaux!!!!");
		}
		return frame;
	}

	public Mat displayHud(Mat frame, int scoreGameP2, int scoreGameP1 ) {
        int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
        double fontScale = 0.5;
        int thickness = 1;

        Point redTeamPosition = new Point(50, 50);
        Point blueTeamPosition = new Point(frame.cols() - 200, 50);
        
        Scalar redColor = new Scalar(0, 0, 255);
        Scalar blueColor = new Scalar(255, 0, 0);

        Imgproc.putText(frame, "Red Team: " + scoreGameP1, redTeamPosition, fontFace, fontScale, redColor, thickness);
        Imgproc.putText(frame, "Blue Team: " + scoreGameP2, blueTeamPosition, fontFace, fontScale, blueColor, thickness);

        return frame; // uselless mais bon peut servir ...
    }

    public Mat displayTarget(Mat frame, Point target) {
        int crossSize = 20; 
        Scalar greenColor = new Scalar(0, 255, 0); 
        int thickness = 2;

        Imgproc.line(
            frame, 
            new Point(target.x - crossSize, target.y), 
            new Point(target.x + crossSize, target.y),
            greenColor, 
            thickness
        );

        Imgproc.line(
            frame, 
            new Point(target.x, target.y - crossSize), 
            new Point(target.x, target.y + crossSize), 
            greenColor, 
            thickness
        );

        return frame; // uselless mais bon ...
    }

}
