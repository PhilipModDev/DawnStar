package com.engine.dawnstar.main.data;

public final class ISingleChunk {
    //The block type for the full chunk.
    public byte block = 0;
    //The local chunk coordinates.
    public int localX;
    public int localY;
    public int localZ;
    //Checks if the chunk is single.
    public boolean isSingle = true;
    //Creates a new single chunk.
    public ISingleChunk(int localX, int localY, int localZ){
        this.localX = localX;
        this.localY = localY;
        this.localZ = localZ;
    }
    //Sets the single data type.
    public void setData(byte block){
        this.block = block;
    }
    //Gets the single data type.
    public byte getData() {
        return block;
    }
}
