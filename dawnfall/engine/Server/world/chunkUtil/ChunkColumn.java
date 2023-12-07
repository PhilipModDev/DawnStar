package com.dawnfall.engine.Server.world.chunkUtil;

import com.dawnfall.engine.Server.world.World;

public class ChunkColumn {
    /*This is the Chunk Column class.*/
    public static final int HEIGHT = World.HEIGHT;
    public Chunk[] chunks;
    public final int xC;
    public final int zC;
    public ChunkColumn(int xC, int zC){
        this.xC = xC;
        this.zC = zC;
        chunks = new Chunk[HEIGHT];
        for (int yLevel = 0; yLevel < HEIGHT; yLevel++) {
            Chunk chunk = new Chunk(xC,yLevel, zC);
            chunks[yLevel] = chunk;
        }
    }
    private boolean isUnsafe(int y) {
        return y < 0 || y >= HEIGHT;
    }
    public Chunk getChunk(int y) {
        return isUnsafe(y) ? null : chunks[y];
    }
}
