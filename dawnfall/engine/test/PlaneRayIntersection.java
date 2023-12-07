package com.dawnfall.engine.test;

import com.badlogic.gdx.math.Vector3;

public class PlaneRayIntersection {
    private static final Vector3 planePos = new Vector3(10,0,0);
    private static final Vector3 planeNor = new Vector3(planePos.x, planePos.y, planePos.z).nor();
    private static final Vector3 rayOrigin = new Vector3(0.5f,0,0);
    private static final Vector3 rayDirection = new Vector3(rayOrigin.x, rayOrigin.y, rayOrigin.z).nor();
    private static final Vector3 pt = new Vector3();
    private static final Vector3 phit  = new Vector3();
    public static boolean isIntersectPlane(){

        double denom = rayDirection.dot(planeNor);
        if (denom > 0.00001d){
            pt.set(planePos.sub(rayOrigin));
            double t = pt.dot(planeNor) / denom;
            if (t >= 0) {
                phit.set((float) (rayOrigin.x + rayDirection.x * t), (float) (rayOrigin.y + rayDirection.y * t), (float) (rayOrigin.z * rayDirection.z * t));
                //Checks whether if it hits the plane's size on which for right now is set to (0.5 * 0.5 * 0.5).
                if (Math.abs(phit.x - planePos.x) <= 0.5 && Math.abs(phit.y - planePos.y) <= 0.5 && Math.abs(phit.z - planePos.z) <= 0.5){
                    System.out.println(phit);
                    return true;
                }
            }
        }

//        float  t = planePos.sub(rayOrigin).dot(planeNor);
//        p.set(rayOrigin.add(rayDirection).set((rayDirection.x*t),(rayDirection.y*t),(rayDirection.z*t)));
//        float denom = planeNor.dot(rayDirection);
//        if (denom > 0.00001){
//            Vector3 p010 = planePos.sub(10);
//            float dot = p.sub(planePos).dot(planeNor);
//            t = p010.dot(planeNor)/denom;
//            return (t >= 0);
//        }
      return false;
    }
}
