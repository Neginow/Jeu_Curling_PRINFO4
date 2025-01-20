package fise2.image4.projetinfo;

import static java.lang.Integer.max;
import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javafx.fxml.FXML;
import org.opencv.core.Mat;


public class GameManager {
    
    public int scoreGameRED;
    public int scoreGameBLUE;
    // EN PLUS
    public int avLive ;
    private Point launchArea;
    // FIN EN PLUS
    public int turnsRemainingRED=8;
    public int turnsRemainingBLUE=8;
    private int totalTurns = 16;

    private Point target;
    private Runnable onScoreUpdate;
    
    private List<Point> centers;
    private List<Integer> radius;
    private List<Boolean> teamid;
    private int turnCounts;
    private List<Point> LaunchAreaPositions;
    
    
    public GameManager() {
        // EN PLUS
        this.launchArea = new Point(0, 0);
        // FIN EN PLUS
        this.target = new Point(0, 0);
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        turnCounts = 0;
    }
    
    public GameManager(Point initialLaunchArea) {
        this.target = new Point(100, 100);
        this.launchArea = initialLaunchArea;
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        turnCounts = 0;
    }
    
    public GameManager(Point initialLaunchArea, Mat frame, int nbTurns) {
        this.launchArea = initialLaunchArea;
        this.target = generateTarget(frame);
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        this.turnsRemainingBLUE = nbTurns;
        this.turnsRemainingRED = nbTurns;
        this.totalTurns = 2*nbTurns;
        turnCounts = 0;
    }
    
    public void resetGame(int nbTurns) {
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        this.turnsRemainingBLUE = nbTurns;
        this.turnsRemainingRED = nbTurns;
        this.totalTurns = 2*nbTurns;
        turnCounts = 0;
    }
    
    public void setTurnsRemaining(boolean isRedTeam) {
        if(isRedTeam) turnsRemainingRED = turnsRemainingRED-1;
        else turnsRemainingBLUE = turnsRemainingBLUE-1;
    }
    
    public void setTurnsRemainingRED(int v) {
        turnsRemainingRED = v;
    }

    public void setTurnsRemainingBLUE(int v) {
        turnsRemainingBLUE = v;
    }
    
    public int getTurnsRemainingRED() {
        return turnsRemainingRED;
    }

    public int getTurnsRemainingBLUE() {
        return turnsRemainingBLUE;
    }
    
    public void setOnScoreUpdate(Runnable callback) {
        this.onScoreUpdate = callback;
    }

    private int circlesOverlapping(Point center, Integer radius) {
        for (int i = 0; i < centers.size(); i++) {
            if (twoCirclesOverlapping(center, radius, this.centers.get(i), this.radius.get(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean twoCirclesOverlapping(Point newCenter, Integer newRadius, Point center, Integer radius) {
        if (newRadius == 0 || radius == 0) return false;
        
        double dist = Math.sqrt(Math.pow(newCenter.x - center.x, 2) + Math.pow(newCenter.y - center.y, 2));
        if (dist >= newRadius + radius) return false;
        
        double A_newCircle = Math.PI * Math.pow(newRadius, 2);
        double t1 = Math.acos((Math.pow(dist, 2) + Math.pow(newRadius, 2) - Math.pow(radius, 2)) / (2 * dist * newRadius));
        double t2 = Math.acos((Math.pow(dist, 2) + Math.pow(radius, 2) - Math.pow(newRadius, 2)) / (2 * dist * radius));
        double t3 = (radius + newRadius - dist) * (dist + newRadius - radius) * (dist - newRadius + radius) * (dist + newRadius + radius);
        t3 = Math.sqrt(t3) / 2;

        double A_intersection = (Math.pow(newRadius, 2) * t1) + (Math.pow(radius, 2) * t2) - t3;
        return A_intersection > 0.05 * A_newCircle;
    }
    private Double dist(Point A, Point B){
    	
    	return Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2));
    }
    public void removeCircle(int i) {
        centers.remove(i);
        radius.remove(i);
        teamid.remove(i);
    }
    
    // Ajout d'un cercle
    public void addCircle(Point newCenter, Integer newRadius) {
        int v = circlesOverlapping(newCenter, newRadius);
        if (v >= 0) {
            removeCircle(v);
        }
        centers.add(newCenter);
        radius.add(newRadius);
        teamid.add(turnCounts % 2 == 0);
        winnerLive();
        turnCounts++;
    } 
    
    private Point generateTarget(Mat frame) {
        
        int width = frame.cols() ;
        int height = frame.rows() ;
        Random random = new Random() ;
        
        // Define the mean (center of the image)
        double meanX;
        double meanY;
        
        if (this.launchArea.x > width/2){
            meanX = width/4.0;
            if (this.launchArea.y > height/2){
                meanY = height/4.0;
            }
            else{
                meanY = 3*height/4.0;
            }
        }
        else{
            meanX = 3*width/4.0;
            if (this.launchArea.y > height/2){
                meanY = height/4.0;
            }
            else{
                meanY = 3*height/4.0;
            }
        }

        // Define the standard deviation (spread of the points)
        double stdDevX = width / 10.0; // Smaller value concentrates points near the center
        double stdDevY = height / 10.0;

        // Generate Gaussian-distributed random values
        double x = Math.max(0, Math.min(width - 1, random.nextGaussian() * stdDevX + meanX));
        double y = Math.max(0, Math.min(height - 1, random.nextGaussian() * stdDevY + meanY));

        return new Point(x, y);
    }
    
    public boolean getWinner() {
        if (centers.isEmpty()) {
            throw new IllegalStateException("Normalement impossible de voir ce message");
        }
        int minIndex = 0; 
        double minDistance = 1000000;
        for (int i = 1; i < centers.size(); i++) {
            double distance = dist(centers.get(i), target);
            if (distance < minDistance) {
                minDistance = distance;
                minIndex = i;
            }
        }
        return teamid.get(minIndex); 
    }

    public boolean isCircleInLaunchArea(Circle token){
        if (token != null){
            Point center = token.getCenter();
            int radius = token.getRadius();
            return ((center.x + radius) <= (launchArea.x + 100))
                    && ((center.x - radius) >= (launchArea.x - 100))
                    && ((center.y + radius) <= (launchArea.y + 100))
                    && ((center.y - radius) >= (launchArea.y - 100));
        }
        else return false;
    }
    
    public boolean isCircleInLaunchArea(Circle token, List<Point> launchAreaPositions){
        if (token != null && !launchAreaPositions.isEmpty()){
            Point center = token.getCenter();
            Point point1 = launchAreaPositions.get(0);
            Point point2 = launchAreaPositions.get(1);
            double width = (Math.max(point1.x,point2.x)-Math.min(point1.x,point2.x));
            double height = (Math.max(point1.y,point2.y)-Math.min(point1.y,point2.y));
            double x = Math.min(point1.x,point2.x);
            double y = Math.min(point1.y,point2.y);
            int radius = token.getRadius();
            return ((center.x + radius) <= (x + width))
                    && ((center.x - radius) >= x)
                    && ((center.y + radius) <= (y + height))
                    && ((center.y - radius) >= y);
        }
        else return false;
    }
    
    public boolean isCircleOutLaunchArea(Circle token, List<Point> launchAreaPositions){
        if (token != null && !launchAreaPositions.isEmpty()){
            Point center = token.getCenter();
            Point point1 = launchAreaPositions.get(0);
            Point point2 = launchAreaPositions.get(1);
            double width = (Math.max(point1.x,point2.x)-Math.min(point1.x,point2.x));
            double height = (Math.max(point1.y,point2.y)-Math.min(point1.y,point2.y));
            double x = Math.min(point1.x,point2.x);
            double y = Math.min(point1.y,point2.y);
            int radius = token.getRadius();
            return ((center.x + radius) <= x)
                    || ((center.x - radius) >= x + width)
                    || ((center.y + radius) <= y)
                    || ((center.y - radius) >= y + height);
        }
        else return false;
    }
    
    // Score Live
    public void winnerLive() {
        if (centers.isEmpty()) {
            throw new IllegalStateException("Normalement impossible de voir ce message");
        }
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < centers.size(); i++) {
            indices.add(i);
        }
        indices.sort((i1, i2) -> Double.compare(
            dist(centers.get(i1), target), 
            dist(centers.get(i2), target)
        ));
        int c = 1; 
        boolean currentTeam = teamid.get(indices.get(0)); 
        
        for (int i = 1; i < indices.size(); i++) {
            if (teamid.get(indices.get(i)) == currentTeam) {
                c++;
            } else {
                break;
            }
        }
        avLive = currentTeam ? c : -c; 
    }

    public void update() {
        if (turnCounts == totalTurns) {
            turnCounts = 0;
            if(avLive < 0) {
                    scoreGameRED -= avLive;
            } else {
                    scoreGameBLUE += avLive;
            }
            avLive = 0 ;    
            centers.clear();
            radius.clear();
            teamid.clear();
            if (onScoreUpdate != null) {
            onScoreUpdate.run(); // Notify score update
            }
        }
    }
    
    public int getAvLive() {
        return avLive;
    }
    
    public List<Point> getCenters() {
        return new ArrayList<>(centers);
    }

    public List<Integer> getRadius() {
        return new ArrayList<>(radius);
    }

    public List<Boolean> getTeamid() {
        return new ArrayList<>(teamid);
    }

    public void setTarget(Point target) {
        this.target = target;
    }

    public Point getTarget() {
        return target;
    }
    
    public String getCurrentTeam() {
        return (turnCounts % 2 == 0) ? "Blue Team" : "Red Team";
    }
    public int getTurnCounts() {
        return turnCounts;
    }
    
    public void setLaunchArea(Point launchArea) {
        this.launchArea = launchArea;
    }

    public Point getLaunchArea() {
        return launchArea;
    }
    
    public void setLaunchAreaPositions(List<Point> LaunchAreaPositions) {
        this.LaunchAreaPositions = LaunchAreaPositions; 
    }
    
    public List<Point> getLaunchAreaPositions() {
        return LaunchAreaPositions;
    }
}
