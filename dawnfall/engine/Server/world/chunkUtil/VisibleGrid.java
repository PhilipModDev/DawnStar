package com.dawnfall.engine.Server.world.chunkUtil;

import com.dawnfall.engine.Server.entity.Player;
import com.dawnfall.engine.Server.util.Utils;
import com.dawnfall.engine.Server.util.math.Direction;

import java.util.*;

public class VisibleGrid {
    public static final Direction[] DIRECTIONS = Direction.values();
    private static final int DX = (int)Math.pow(16.0D, 0.0D);
    private static final int DZ = (int)Math.pow(16.0D, 1.0D);
    private static final int DY = (int)Math.pow(16.0D, 2.0D);
    private int empty = 4096;
    private final BitSet bitSet = new BitSet(Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE);
    public final Player player;
    public final ChunkManager chunkManager;
    public static final int[] INDEX;

     static {
         INDEX = new int[1536];
         int i = 0;
         for (int x = 0; x < Chunk.CHUNK_SIZE; x++) {
             for (int y = 0; y < Chunk.CHUNK_SIZE; y++) {
                 for (int z = 0; z < Chunk.CHUNK_SIZE; z++) {
                    if (x == 0 || x == 15 || y == 0 || y == 15 || z == 0 || z == 15){
                        INDEX[i++] = Utils.xyzToIndex(x,y,z);
                    }
                 }
             }
         }
     }
    public VisibleGrid(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
        this.player = chunkManager.worldRenderer.player;
    }
    public void setSolidBlock(int x, int y, int z){
        bitSet.set(Utils.xyzToIndex(x, y, z),true);
        empty--;
    }
    public ChunkOcclusion.VisibilityInfo floodFillChunk(){
        ChunkOcclusion.VisibilityInfo visibilityInfo = new ChunkOcclusion.VisibilityInfo();
         if (4096 - empty < 256){
             //Checks whether if the chunk contains more air blocks.
             visibilityInfo.setAll(true);
         }else if (empty == 0){
             //Checks whether if the chunk is solid.
            visibilityInfo.setAll(false);
         }else {
             for (int index : INDEX){
                 if (!bitSet.get(index)){
                     visibilityInfo.setFacesVisible(floodFill3d(index));
                 }
             }
         }
         return visibilityInfo;
    }
    public Set<Direction> floodFill3d(int first){
        Set<Direction> directions = EnumSet.noneOf(Direction.class);
        Queue<Integer> indexQueue = new ArrayDeque<>();
        indexQueue.add(first);
        bitSet.set(first,true);
        while (!indexQueue.isEmpty()){
            int idx = indexQueue.poll();
            int x = idx & 15;
            int y = idx >> 8 & 15;
            int z = idx >> 4 & 15;
            addEdges(x,y,z,directions);
            for (Direction direction : Direction.values()){
                int index = getNeighborIndexAtFace(idx,direction);
                if (index >= 0 && !bitSet.get(index)) {
                    bitSet.set(index,true);
                    indexQueue.add(index);
                }
            }
        }
        return directions;
    }
    private void addEdges(int x , int y, int z, Set<Direction> statuses){
        if (x == 0) statuses.add(Direction.WEST);
        if (x == 15) statuses.add(Direction.EAST);
        if (y == 0) statuses.add(Direction.DOWN);
        if (y == 15) statuses.add(Direction.UP);
        if (z == 0) statuses.add(Direction.SOUTH);
        if (z == 15) statuses.add(Direction.NORTH);
    }
    private int getNeighborIndexAtFace(int index, Direction p_112967_) {
        switch (p_112967_) {
            case DOWN:
                if ((index >> 8 & 15) == 0) {
                    return -1;
                }

                return index - DY;
            case UP:
                if ((index >> 8 & 15) == 15) {
                    return -1;
                }

                return index + DY;
            case NORTH:
                if ((index >> 4 & 15) == 0) {
                    return -1;
                }

                return index - DZ;
            case SOUTH:
                if ((index >> 4 & 15) == 15) {
                    return -1;
                }

                return index + DZ;
            case WEST:
                if ((index & 15) == 0) {
                    return -1;
                }

                return index - DX;
            case EAST:
                if ((index & 15) == 15) {
                    return -1;
                }

                return index + DX;
            default:
                return -1;
        }
    }
}
