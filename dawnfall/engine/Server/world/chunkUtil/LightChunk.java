package com.dawnfall.engine.Server.world.chunkUtil;

import com.dawnfall.engine.Server.util.math.Direction;
import com.dawnfall.engine.Server.world.Blocks.Blocks;

import java.util.Arrays;
import java.util.BitSet;

public class LightChunk {
    public static final int MAX_LIGHT = 15;
    public static final int MIN_LIGHT = 0;
    private ChunkManager chunkManager;
    public LightChunk(ChunkManager chunkManager){
        this.chunkManager = chunkManager;
    }
    public void updateChunkLighting(Chunk chunk) {
        if (chunk == null) return;
        BitSet lightSet = new BitSet();
        byte[] lightData = chunk.getLightData();
        for (int i = 0; i < lightData.length; i++) {
            int x = i & 15;
            int y = i >> 8 & 15;
            int z = i >> 4 & 15;
            if (canAddLight(x,y,z,chunk)){
                setLight(x,y,z,1231,lightData);
                System.out.println(Arrays.toString(lightData));
                 lightSet.set(i,true);
                 setLight(x,y,z,1,lightData);
                for (Direction direction : Direction.values()) {
                    if (direction.equals(Direction.NORTH) && canAddLight(x,y,z + Direction.NORTH.v,chunk)){
                        setLight(x,y,z,1,lightData);
                    }
                    if (direction.equals(Direction.SOUTH) && canAddLight(x,y,z + Direction.SOUTH.v,chunk)){
                        setLight(x,y,z,1,lightData);
                    }
                    if (direction.equals(Direction.EAST) && canAddLight(x+ Direction.EAST.v,y,z,chunk)){
                        setLight(x,y,z,1,lightData);
                    }
                    if (direction.equals(Direction.WEST) && canAddLight(x+ Direction.WEST.v,y,z,chunk)){
                        setLight(x,y,z,1,lightData);
                    }
                    if (direction.equals(Direction.UP) && canAddLight(x,y+ Direction.UP.v,z,chunk)){
                        setLight(x,y,z,1,lightData);
                    }
                    if (direction.equals(Direction.DOWN) && canAddLight(x,y+ Direction.DOWN.v,z,chunk)){
                        setLight(x,y,z,1,lightData);
                    }
                }
            }
        }

    }

    private boolean canAddLight(int x, int y, int z,Chunk chunk){
        return chunk.getBlockAt(x, y, z) == Blocks.LIGHT;
    }

    private void setLight(int x, int y, int z,int value, byte[] lights){
        lights[x | y << 8 | z << 4] = (byte) value;
    }
}
