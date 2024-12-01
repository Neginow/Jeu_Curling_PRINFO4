package com.example.curling.vision;

import org.opencv.core.Point;
import java.util.ArrayList;
import java.util.List;

public class GameManager {

	private int scoreGameP1 ;
	private int scoreGameP2 ;
	
	private int scorePlayer1 ;
	private int scorePlayer2 ;
	
	private int currentPlayer = 1;
	private Point target ;
	
	private List<Double> distance1 ;
    private List<Double> distance2 ;
    
    public GameManager() {
    	scorePlayer1 = 0 ;
    	scorePlayer2 = 0 ;
    	currentPlayer = 1 ;
    	distance1 = new ArrayList<>() ;
    	distance2 = new ArrayList<>() ;
    }
    
    public void switchTurn() {
        currentPlayer = (currentPlayer == 1) ? 2 : 1;
    }
    
	public void scoreCaclul() {
		// TODO
	}

	public void calculateDistance(Point token) {
		Double newDist = Math.sqrt(Math.pow(target.x - token.x, 2) + Math.pow(target.y - token.y, 2));
		
		if (currentPlayer == 1) {
			distance1.add(newDist) ;
		}
		else {
			distance2.add(newDist) ;
		}
	}
}
