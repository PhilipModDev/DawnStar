package com.dawnfall.engine.Server.world.chunkUtil;

import com.dawnfall.engine.Server.util.math.Direction;
import java.util.BitSet;
import java.util.Set;


public class VisibilitySet {
    public final int MAX_FACING = Direction.values().length;
    public final BitSet bitsetFaces = new BitSet(MAX_FACING);
    public final int x;
    public final int y;
    public final int z;
    public VisibilitySet(int x, int y, int z){
       this.x = x;
       this.y = y;
       this.z = z;
    }
    public void setDirections(Set<Direction> directions) {
        for (Direction directionTypes : directions) {
            switch (directionTypes){
                case NORTH : bitsetFaces.set(0,true);
                    break;
                case SOUTH : bitsetFaces.set(1,true);
                    break;
                case EAST : bitsetFaces.set(2,true);
                    break;
                case WEST : bitsetFaces.set(3,true);
                    break;
                case UP : bitsetFaces.set(4,true);
                    break;
                case DOWN : bitsetFaces.set(5,true);
            }
        }
    }
    public void setDirection(Direction direction) {
        bitsetFaces.set(direction.ordinal(),true);
    }
    public boolean isConnectedFaces(Direction direction1, Direction direction2){
        return Direction.getOpposite(direction1).equals(direction2);
    }
    public boolean isVisibleDirection(Direction direction){
      return bitsetFaces.get(direction.ordinal());
    }
    public boolean isConnectedFaces(VisibilitySet set){
        for (Direction direction : VisibleGrid.DIRECTIONS){
            boolean A =  isVisibleDirection(direction);
            boolean B = set.isVisibleDirection(Direction.getOpposite(direction));
            if (A && B) return true;
        }
        return false;
    }
}
