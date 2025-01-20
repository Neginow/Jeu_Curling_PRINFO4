package fise2.image4.projetinfo;

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
