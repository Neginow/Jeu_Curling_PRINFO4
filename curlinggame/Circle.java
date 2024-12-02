package testopenCV;

import org.opencv.core.Point;

public class Circle {
    private Point center;
    private int radius;

    // Constructor
    public Circle(Point center, int radius) {
        this.center = center;
        this.radius = radius;
    }

    // Getters
    public Point getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    // toString method for debugging purposes
    @Override
    public String toString() {
        return "Circle{Center: " + center + ", Radius: " + radius + "}";
    }
}
