package com.engine.dawnstar.main.data;

public final class Chunk {
    //Chunk size.
    public static final int SIZE = 32;
    //The local chunk position.
    public int localX;
    public int localY;
    public int localZ;
    public boolean isVisible = false;
    public boolean isDirty = false;
    public boolean isLight = false;
    //The chunk data layers.
    public IChunkLayer[] chunkLayers;
    //The chunk data single type.
    public ISingleChunk singleChunk;

    public Chunk(int localX, int localY, int localZ){
        this.localX = localX;
        this.localY = localY;
        this.localZ = localZ;
    }

    //Gets the block at the coordinates.
    public byte getBlock(int x, int y, int z){
        if (y < 0 || y >= SIZE) return 0;
        if (x < 0 || x >= SIZE) return 0;
        if (z < 0 || z >= SIZE) return 0;
        if (singleChunk == null) {
            singleChunk = new ISingleChunk(localX,localY,localZ);
            return singleChunk.getData();
        }
        if (singleChunk.isSingle){
            return singleChunk.getData();
        }
        IChunkLayer chunkLayer = chunkLayers[y];
        if (chunkLayer == null) {
            chunkLayers[y] = new IChunkLayer(y,singleChunk.getData());
        }
        return chunkLayers[y].getData(x, z);
    }
    //Sets the block at the coordinates.
    public void setBlock(int x, int y, int z,byte block){
        if (y < 0 || y >= SIZE) return;
        if (singleChunk == null) {
            singleChunk = new ISingleChunk(localX,localY,localZ);
            singleChunk.setData(block);
            return;
        }

        if (singleChunk.isSingle && block != singleChunk.getData()){
            singleChunk.isSingle = false;
            chunkLayers = new IChunkLayer[SIZE];
            IChunkLayer chunkLayer = chunkLayers[y];
            if (chunkLayer == null) chunkLayers[y] = new IChunkLayer(y,singleChunk.getData());
            chunkLayers[y].setData(x, z, block);
            return;
        }

        if (singleChunk.isSingle) {
           singleChunk.setData(block);
           return;
        }

        IChunkLayer chunkLayer = chunkLayers[y];
        if (chunkLayer == null) chunkLayers[y] = new IChunkLayer(y,singleChunk.getData());
        chunkLayers[y].setData(x, z, block);
    }
}
