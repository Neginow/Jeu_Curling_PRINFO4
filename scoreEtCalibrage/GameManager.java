/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetinfo.projetinfo;

import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class GameManager {

    public int scoreGameRED;
    public int scoreGameBLUE;
    
    public int avLive ;
    public Boolean winnerLive ;

    private Point target;

    private final List<Point> centers;
    private final List<Integer> radius;
    private final List<Boolean> teamid;
    private int turnCounts;
    
    // Constructeurs
    public GameManager() {
        this.target = new Point(0, 0);
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        turnCounts = 0;
    }

    public GameManager(Point initialTarget) {
        this.target = initialTarget;
        this.centers = Collections.synchronizedList(new ArrayList<>());
        this.radius = Collections.synchronizedList(new ArrayList<>());
        this.teamid = Collections.synchronizedList(new ArrayList<>());
        this.scoreGameRED = 0;
        this.scoreGameBLUE = 0;
        turnCounts = 0;
    }

    // Gestion des chevauchements
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
    
    // Calcul de la distance entre deux points
    private Double dist(Point A, Point B){
    	
    	return Math.sqrt(Math.pow(A.x - B.x, 2) + Math.pow(A.y - B.y, 2));
    }
    
    // Suppression d'un cercle
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
        turnCounts++;
    } 
    
    // Score Live
    public void winnerLive() {
        if (centers.isEmpty()) {
            throw new IllegalStateException("Normalement impossible de voir ce message");
        }
        
        // Tri des teamid en fonction de la distance à la cible
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < centers.size(); i++) {
            indices.add(i);
        }
        indices.sort((i1, i2) -> Double.compare(
            dist(centers.get(i1), target), 
            dist(centers.get(i2), target)
        ));
        
        List<Boolean> teamidSorted = new ArrayList<>() ;
        
        for (int index : indices) {
            teamidSorted.add(teamid.get(index));
        }

        Boolean winner = teamidSorted.get(0) ;
        System.out.println(winner);
        int i = 0 ;
        int avantage = 0 ;
        
        while (Objects.equals(winner, teamid.get(i)) && i<teamid.size()) {
            avantage ++ ;
        }
        
        avLive = avantage ;
        winnerLive = winner ;
    }
    

    // Passage à la partie suivante
    public void update() {
        if (turnCounts == 3) {
        	turnCounts = 0;
        	if(winnerLive) {
        		scoreGameRED += avLive;
        	} else {
        		scoreGameBLUE += avLive ;
        	}
                avLive = 0 ;
                winnerLive = null ;
                
        	centers.clear();
        	radius.clear();
        	teamid.clear();
        }
    }
    
    
    
    
    
    // Getters Setters
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
}
