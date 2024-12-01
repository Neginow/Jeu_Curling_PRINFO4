package com.example.curling.vision;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;

/*
 * Classe qui permet de détecter la pièce chaque tour. Elle permet aussi de mettre a jour la liste des distances et des positions de l'ensemble des pièces. 
 * Elle gère également les chevauchements.
 */

public class TokenDetector {
	
	private Mat background ;		// fond en niveau de gris
	private List<Integer> range ;	// bornes de recherche pour Hough
	
	private List<java.awt.Point> centers1 ;	// Centres détectés de l'équipe 1
	private List<Integer> radius1 ;
	
	private List<Point> centers2 ;	// Centres détectés de l'équipe 2
	private List<Integer> radius2 ;
	
	private GameManager gm ;
	
	public void detectToken(Mat frame, int currentPlayer) {
       // Initialisation
        Mat grayFrame = new Mat();
        Mat absDiff = new Mat() ;
        Mat circles = new Mat() ;
        
        // Passage en ndg et différence absolue avec le fond
        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);
        Core.absdiff(grayFrame, background, absDiff) ;
        
        // Transformée de Hough pour récupérer la pièce
        Imgproc.HoughCircles(
        		absDiff, 
        		circles, 
        		Imgproc.HOUGH_GRADIENT, 
        		1, 					// résolution
        		3,					// Distance minimale entre deux centres
        		100, 			// Seuil pour Canny
        		50, 			// Seuil pour détection du centre
        		this.range.get(0),  // minRadius
        		this.range.get(1)   // maxRadius
        ); // Normalement on récupère qu'un cercle
        
        
        // Récupération du centre et du rayon
        double[] circle = circles.get(0, 0); // Obtenir les données du cercle
        Point center = new Point(circle[0], circle[1]); // Centre (x, y)
        int radius = (int) circle[2]; // Rayon du cercle
        
        // Vérification des chevauchements
        overlap(center, radius) ;
        
        // Ajout dans la bonne liste selon le joueur
        if (currentPlayer == 1) {
        	centers1.add(center) ;
        	radius1.add(radius) ;
        }
        else {
        	centers2.add(center) ;
        	radius2.add(radius) ;
        }
        gm.calculateDistance(center) ;
        
        // Affichage des cercles
        displayCircles(frame) ;
        
    }
	
	public void displayCircles(Mat frame) {
		for (Point p : centers1) {
			Imgproc.circle(frame, p, radius, new Scalar(255, 0, 0), -1);
		}
		for (Point p : centers2) {
			Imgproc.circle(frame, p, radius, new Scalar(0, 0, 255), -1);
		}
	}
	
	public void overlap(Point center, Integer radius) {
		// Initialisation
		List<Integer> indices1 = new ArrayList<>() ;
		List<Integer> indices2 = new ArrayList<>() ;
		
		// Récupération des indices à supprimer
		for (int i=0; i < centers1.size() ; i++ ) {
			if ( OverlapTest(center, radius, centers1.get(i), radius1.get(i)) ) {
				indices1.add(i) ;
			}
		}
		for (int i=0; i < centers2.size() ; i++ ) {
			if ( overlapTest(center, radius, centers2.get(i), radius2.get(i)) ) {
				indices2.add(i) ;
			}
		}
		
		// Suppression des cercles correspondants
		for (Integer i : indices1) {
			centers1.remove(i) ;
			radius1.remove(i) ;
			gm.removeDistance(i, 1) ;
		}
		for (Integer i : indices2) {
			centers1.remove(i) ;
			radius1.remove(i) ;
			gm.removeDistance(i, 2);
		}	
	}
	
	private boolean overlapTest(Point newCenter, Integer newRadius, Point center, Integer radius) {
		// Vérifie la condition de chevauchement, cercle à supprimer si renvoie true
		Double A_newCircle = Math.PI* Math.pow(newRadius, 2) ;
		Double dist = Math.sqrt(Math.pow(newCenter.x - center.x, 2) + Math.pow(newCenter.y - center.y, 2));
		
		Double t1 = Math.acos((Math.pow(dist, 2) + Math.pow(newRadius, 2) - Math.pow(radius, 2))/(2*dist*newRadius)) ;
		Double t2 = Math.acos((Math.pow(dist, 2) + Math.pow(radius, 2) - Math.pow(newRadius, 2))/(2*dist*radius)) ;
		
		Double t3 = (radius + newRadius - dist)*(dist + newRadius - radius)*(dist - newRadius + radius)*(dist + newRadius + radius) ;
		t3 = Math.sqrt(t3)/2 ;
		
		Double A_intersection = (Math.pow(newRadius, 2)*t1) + (Math.pow(radius, 2)*t2) - t3 ;
		
		return A_intersection > 0.05*A_newCircle ;
	}
	
}
