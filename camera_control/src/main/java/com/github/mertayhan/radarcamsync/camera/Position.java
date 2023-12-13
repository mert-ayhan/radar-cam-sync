package com.github.mertayhan.radarcamsync.camera;

public class Position {

    private double x;
    private double y;

    public Position(double x, double y) {
        this.setX(x);
        this.setY(y);
    }

    public Position(double[] coords) {
        this.setX(coords[0]);
        this.setY(coords[1]);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double calculateDistanceTo(Position other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    public double calculateAngleTo(Position other) {
        return (Math.atan2(other.y - this.y, other.x - this.x) * 180) / Math.PI;
    }

    public Position findPositionFromViewpoint(Viewpoint viewpoint) {
        double angleRad = Math.toRadians(viewpoint.getAngle());
        double newX = this.x + viewpoint.getDistance() * Math.cos(angleRad);
        double newY = this.y + viewpoint.getDistance() * Math.sin(angleRad);

        return new Position(newX, newY);
    }
}
