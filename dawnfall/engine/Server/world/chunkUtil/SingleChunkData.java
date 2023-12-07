package com.dawnfall.engine.Server.world.chunkUtil;

public class SingleChunkData implements ChunkDataStorage {

    public Chunk chunk;
    private byte blockId;
    public SingleChunkData(Chunk chunk,byte blockId){
        this.blockId = blockId;
    }
    @Override
    public byte getBlockAt(int x, int y, int z) {
        return blockId;
    }
    @Override
    public void setBlockAt(int x, int y, int z, byte blockId) {
       this.blockId = blockId;
    }
}
