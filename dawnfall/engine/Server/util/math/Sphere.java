package com.dawnfall.engine.Server.util.math;

public class Sphere {
    public int x;
    public int y;
    public int z;
    private final float radius;
    public Sphere(int x, int y, int z,int radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }
    public Sphere(int radius) {
        this.radius = radius;
    }
    public Sphere setPositions(int x, int y,int z){
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    public boolean isPointInSphere(int px, int py, int pz){
        return Math.pow(px - x,2) + Math.pow(py - y,2)  + Math.pow(pz - z,2) <= Math.pow(radius,2);
    }
    public boolean isAtEdgePoint(int px,int py, int pz){
        double rad  = Math.pow(radius-1,2);
        double pth = Math.pow(px - x,2) + Math.pow(py - y,2) + Math.pow(pz - z,2);
        return pth > rad && pth <= Math.pow(radius, 2);
    }
    public float getRadius() {
        return radius;
    }
}
