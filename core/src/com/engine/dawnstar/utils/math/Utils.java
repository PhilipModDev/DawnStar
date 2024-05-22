package com.engine.dawnstar.utils.math;

import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.engine.dawnstar.main.data.Chunk;

public class Utils {

    public static int manhattanDistanceChunks(Chunk chunkOne, Chunk chunkTwo){
        if (chunkOne == null || chunkTwo == null) return -1;
        int distanceX = Math.abs(chunkTwo.localX - chunkOne.localX);
        int distanceY = Math.abs(chunkTwo.localY - chunkOne.localY);
        int distanceZ = Math.abs(chunkTwo.localZ - chunkOne.localZ);
        return distanceX + distanceY + distanceZ;
    }

    public static int manhattanDistanceGrid(GridPoint3 pointOne,GridPoint3 pointTwo){
        if (pointOne == null || pointTwo == null) return -1;
        int distanceX = Math.abs(pointTwo.x - pointOne.x);
        int distanceY = Math.abs(pointTwo.y - pointOne.y);
        int distanceZ = Math.abs(pointTwo.z - pointOne.z);
        return distanceX + distanceY + distanceZ;
    }

    public static float euclidDistance(Vector3 pointOne,Vector3 pointTwo){
        if (pointOne == null || pointTwo == null) return -1f;
        return pointTwo.dst(pointOne);
    }
}
