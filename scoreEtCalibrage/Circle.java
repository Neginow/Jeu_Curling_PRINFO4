/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package projetinfo.projetinfo;

import org.opencv.core.Point;

public class Circle {
	private Point center;
	private int radius;

	public Circle(Point center, int radius) {
		this.center = center;
		this.radius = radius;
	}
        
	public Point getCenter() {
		return center;
	}

	public int getRadius() {
		return radius;
	}

	@Override
	public String toString() {
		return "Circle{Center: " + center + ", Radius: " + radius + "}";
	}
}
