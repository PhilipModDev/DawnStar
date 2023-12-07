package com.dawnfall.engine.Server.util.math;

import com.badlogic.gdx.math.Vector3;
import java.util.Objects;

public enum Direction {
    /**
     * The enums representing the values of
     * the directions facing.
     */
    NORTH(1),//z
    SOUTH(-1),//z
    EAST(1),//x
    WEST(-1),//x
    UP(1),//y
    DOWN(-1);//y
    public final int v;
    Direction(int v) {
        this.v = v;
    }
    public static Direction getOpposite(Direction direction){
        if (direction.equals(NORTH)) return Direction.SOUTH;
        if (direction.equals(SOUTH)) return Direction.NORTH;
        if (direction.equals(EAST))  return Direction.WEST;
        if (direction.equals(WEST))  return Direction.EAST;
        if (direction.equals(UP))    return Direction.DOWN;
        if (direction.equals(DOWN))  return Direction.UP;
        return direction;
    }
    public static Direction getOpposite(Vector3 directionVec3){
        return getOpposite(Objects.requireNonNull(vec3ToDirection(directionVec3)));
    }
    public static Direction vec3ToDirection(Vector3 vector3){
        vector3.nor();
        float x = Math.abs(vector3.x);
        float y = Math.abs(vector3.y);
        float z = Math.abs(vector3.z);
        float threshold = 0.00001f;
        if (x > y && x > z) {
            return vector3.x > threshold ? Direction.EAST : Direction.WEST;
        } else if (y > x && y > z) {
            return vector3.y > threshold ? Direction.UP : Direction.DOWN;
        } else {
            return vector3.z > threshold ? Direction.NORTH : Direction.SOUTH;
        }
    }
    public static Vector3 directionToVec3(Direction direction){
        if (direction.equals(Direction.NORTH) || direction.equals(Direction.SOUTH)) return new Vector3(0,0,direction.v);
        if (direction.equals(Direction.EAST)  || direction.equals(Direction.WEST)) return new Vector3(direction.v,0,0);
        if (direction.equals(Direction.DOWN)  || direction.equals(Direction.UP)) return new Vector3(0,direction.v,0);
        return new Vector3(0,0,0);
    }
}
