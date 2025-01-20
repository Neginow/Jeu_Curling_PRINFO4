package fise2.image4.projetinfo;

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

public class TokenDetectorbis {

	private Mat background;
	private List<Integer> range;
        private Integer fixedRadius;

	public TokenDetectorbis(Mat background, List<Integer> range) {
		setBackground(background);
		this.range = range;
                this.fixedRadius = null;
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

        public Point detectTokenWithCanny(Mat frame) {
            Mat grayFrame = new Mat();
            Mat edges = new Mat();
            
            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            Core.absdiff(grayFrame, background, grayFrame);

            Imgproc.Canny(grayFrame, edges, 50, 150);

            List<MatOfPoint> contours = new ArrayList<>();
            Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

            Point centerPoint = null;

            if (centerPoint != null) {
                return centerPoint;
            }
            return null;
        }

        public Circle detectToken(Mat frame) {
            Mat grayFrame = new Mat();
            Mat blackHat = new Mat();
            Mat blurred = new Mat();
            Mat background = new Mat();
            Mat circles = new Mat();
            Mat morphKernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new org.opencv.core.Size(10, 10));

            Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

            Imgproc.morphologyEx(grayFrame, blackHat, Imgproc.MORPH_BLACKHAT, morphKernel);
            Core.subtract(grayFrame, blackHat, grayFrame);
            Mat m = increaseContrast(grayFrame, 2.0, 5.);

            Imgproc.GaussianBlur(m, blurred, new org.opencv.core.Size(7, 7), 1.5, 1.5);

            if (fixedRadius == null) {
                    Imgproc.HoughCircles(blurred, circles, Imgproc.HOUGH_GRADIENT, 1.5, 30, 100, 0.9, 15, 30);
            } else {
                    int minRadius = (int) (fixedRadius * 0.5);
                    int maxRadius = (int) (fixedRadius * 1.5);
                    Imgproc.HoughCircles(blurred, circles, Imgproc.HOUGH_GRADIENT, 1.5, 30, 100, 0.9, 15, 30);
            }

            if (!circles.empty()) {
                //System.out.println("circles!");
                double[] circleData = circles.get(0, 0);
                if (circleData != null && circleData.length >= 3) {
                    int x = (int) circleData[0];
                    int y = (int) circleData[1];
                    int radius;
                    if (fixedRadius == null) {
                            radius = (int) circleData[2];
                    } else {
                            radius = (int) fixedRadius;
                    }
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

	public Mat displayHud(Mat frame, int scoreGameBlue, int scoreGameRed,int advantage  ) {
        int fontFace = 0;
        double fontScale = 0.5;
        int thickness = 1;

        Point redTeamPosition = new Point(50, 50);
        Point blueTeamPosition = new Point(frame.cols() - 200, 50);
        Point advantagePosition = new Point(frame.cols() / 2 - 70, 50);
        Scalar redColor = new Scalar(0, 0, 255);
        Scalar blueColor = new Scalar(255, 0, 0);
        Scalar advColor = new Scalar(0, 255, 0);
        
        if (advantage > 0) {
            advColor = blueColor;
        }
        
        if (advantage < 0) {
                advColor = redColor;
        }
        
        Imgproc.putText(frame, "Advantage : " + Math.abs(advantage), advantagePosition, fontFace, fontScale, advColor,
				thickness);
        Imgproc.putText(frame, "Red Team: " + scoreGameRed, redTeamPosition, fontFace, fontScale, redColor, thickness);
        Imgproc.putText(frame, "Blue Team: " + scoreGameBlue, blueTeamPosition, fontFace, fontScale, blueColor, thickness);
        

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

    // Affichage de la zone de lancer
        public Mat displayLaunchArea(Mat frame, Point launchArea){
            int zoneSize=150;
            Scalar greenColor = new Scalar(0, 255, 0);
            int thickness = 2;
            
            Imgproc.line(frame, new Point(launchArea.x - zoneSize/2, launchArea.y - zoneSize/2), new Point(launchArea.x + zoneSize/2, launchArea.y - zoneSize/2),
				greenColor, thickness);
            Imgproc.line(frame, new Point(launchArea.x - zoneSize/2, launchArea.y + zoneSize/2), new Point(launchArea.x + zoneSize/2, launchArea.y + zoneSize/2),
				greenColor, thickness);
            Imgproc.line(frame, new Point(launchArea.x - zoneSize/2, launchArea.y - zoneSize/2), new Point(launchArea.x - zoneSize/2, launchArea.y + zoneSize/2),
				greenColor, thickness);
            Imgproc.line(frame, new Point(launchArea.x + zoneSize/2, launchArea.y - zoneSize/2), new Point(launchArea.x + zoneSize/2, launchArea.y + zoneSize/2),
				greenColor, thickness);
            
            return frame; // uselless mais bon ...
        }
        
        public Mat displayLaunchArea(Mat frame, List<Point> launchAreaPositions){
            
            Point point1 = launchAreaPositions.get(0);
            Point point2 = launchAreaPositions.get(1);
            
            Scalar greenColor = new Scalar(0, 255, 0);
            int thickness = 2;
            
          
            Imgproc.rectangle(frame, new Point(Math.min(point1.x,point2.x), Math.min(point1.y,point2.y)), new Point(Math.max(point1.x,point2.x), Math.max(point1.y,point2.y)),
				greenColor, thickness);
            
            return frame; // uselless mais bon ...
        }
        
        public void fixRadius(Mat frame) {
		Mat grayFrame = new Mat();
		Mat absDiff = new Mat();
		Mat blurred = new Mat();
		Mat circles = new Mat();

		Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

		Core.absdiff(grayFrame, background, absDiff);

		Imgproc.GaussianBlur(absDiff, blurred, new org.opencv.core.Size(9, 9), 6);
		Mat m = increaseContrast(blurred, 10.0, 5.);

		Imgproc.HoughCircles(m, circles, Imgproc.HOUGH_GRADIENT, 1.3, 30, 150, 30, 0, 200);

		if (!circles.empty()) {
			System.out.println("Found !");
			double[] circleData = circles.get(0, 0);
			if (circleData != null && circleData.length >= 3) {

				fixedRadius = (int) circleData[2];
                                System.out.println(fixedRadius);

			}
		}

	}
}
