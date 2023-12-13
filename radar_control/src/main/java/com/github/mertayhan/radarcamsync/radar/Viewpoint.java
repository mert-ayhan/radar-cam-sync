package com.github.mertayhan.radarcamsync.radar;

public class Viewpoint {

    private double angle;
    private double distance;

    public Viewpoint(double angle, double distance) {
        this.angle = angle;
        this.distance = distance;
    }

    public Viewpoint(double[] angleAndDistance) {
        this.angle = angleAndDistance[0];
        this.distance = angleAndDistance[1];
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return angle + "," + distance;
    }
}
