package com.ui.lib;
public class Vector2 {
    private double x;
    private double y;

    public Vector2() {
        x = y = 0;
    }

    public Vector2(double x, double y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}