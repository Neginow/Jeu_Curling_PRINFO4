package testopenCV;


import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.List;

/*
 * Classe qui gère le score de la manche et le score de la partie. 
 * Elle contient les scores, la position de la cible, à qui est le tour de jouer ainsi que les listes des distances à la cible pour calculer le score.
 */

public class GameManager {

	private int scoreGameP1;
	private int scoreGameP2;

	private Point target;
	
	private List<Point> centers;	
	private List<Integer> radius;
	private List<Boolean> teamid;
	
    
    public GameManager() {
    	target  = new Point(0,0);
    	centers = new ArrayList<>();
    	radius  = new ArrayList<>();
    	teamid  = new ArrayList<>();
    	scoreGameP1 = 0;
    	scoreGameP2 = 0;
    }
        
	public void scoreCaclul() {
		
	}

	public int overlap(Point center, Integer radius) {
		//for (int i = 0; i < )
		
		
		return -1;
	}
	
	private boolean overlapTest(Point newCenter, Integer newRadius, Point center, Integer radius) {
		Double A_newCircle = Math.PI* Math.pow(newRadius, 2);
		Double dist = Math.sqrt(Math.pow(newCenter.x - center.x, 2) + Math.pow(newCenter.y - center.y, 2));
		
		Double t1 = Math.acos((Math.pow(dist, 2) + Math.pow(newRadius, 2) - Math.pow(radius, 2))/(2*dist*newRadius));
		Double t2 = Math.acos((Math.pow(dist, 2) + Math.pow(radius, 2) - Math.pow(newRadius, 2))/(2*dist*radius));
		
		Double t3 = (radius + newRadius - dist)*(dist + newRadius - radius)*(dist - newRadius + radius)*(dist + newRadius + radius) ;
		t3 = Math.sqrt(t3)/2 ;
		
		Double A_intersection = (Math.pow(newRadius, 2)*t1) + (Math.pow(radius, 2)*t2) - t3 ;
		
		return A_intersection > 0.05 * A_newCircle ;
	}

	
	public Double calculateDistance(Point token) {
		return Math.sqrt(Math.pow(target.x - token.x, 2) + Math.pow(target.y - token.y, 2));
	}
	
	public void removeDistance(int i) {
		centers.remove(i);
		radius.remove(i);
		teamid.remove(i);
	}
	
	public void addCircle(Point newCenter, Integer newRadius) {
		int v = overlap(newCenter, newRadius);
		// on verifie si il y a un cercle par dessus l'autre
		if (v > 0) {
			//removeDistance();
		}
		
	}
	
	public List<Point> getCenters() {
		return centers;
	}

	public List<Integer> getRadius() {
		return radius;
	}

	public List<Boolean> getTeamid() {
		return teamid;
	}


}
