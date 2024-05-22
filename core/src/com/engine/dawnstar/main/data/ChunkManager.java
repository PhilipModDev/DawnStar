package com.engine.dawnstar.main.data;

import com.engine.dawnstar.main.blocks.Blocks;
import java.util.Hashtable;

public final class ChunkManager {
    private final Hashtable<ChunkPos, Chunk> chunkStorage;
    public ChunkManager(){
        chunkStorage = new Hashtable<>();
    }

    public void addToStorage(Chunk chunk){
        ChunkPos chunkPos = new ChunkPos(chunk.localX,chunk.localY,chunk.localZ);
        chunkStorage.put(chunkPos,chunk);
    }

    public Chunk getChunkAt(int x,int y,int z){
        ChunkPos chunkPos = new ChunkPos(x,y,z);
        return chunkStorage.get(chunkPos);
    }

    public Chunk getChunkAt(ChunkPos chunkPos){
        return chunkStorage.get(chunkPos);
    }

    public Chunk getChunk(int px,int py, int pz){
        return chunkStorage.get(new ChunkPos(px >> 5, py >> 5,pz >> 5));
    }

    public byte getBlockAt(int px,int py, int pz){
        Chunk chunk = getChunk(px, py , pz );
        if (chunk == null) return Blocks.AIR.get().id;
        return chunk.getBlock(px & 31, py & 31, pz & 31);
    }

    public void deleteChunkAt(ChunkPos chunkPos){
        chunkStorage.remove(chunkPos);
    }

    public void deleteChunkAt(int x,int y, int z){
        ChunkPos chunkPos = new ChunkPos(x,y,z);
        chunkStorage.remove(chunkPos);
    }

    public boolean sixChunkCheck(int x,int y,int z){
        //Temp for infinite world gen.
        if (x <= -World.WORD_SIZE || x >= World.WORD_SIZE) return false;
        if (y <= -World.WORD_SIZE || y >= World.WORD_SIZE) return false;
        if (z <= -World.WORD_SIZE || z >= World.WORD_SIZE) return false;

        ChunkPos pos = new ChunkPos(x,y,z + 1);
        Chunk north = chunkStorage.get(pos);
        if (north == null) return false;
        pos.set(x, y, z - 1);
        Chunk south = chunkStorage.get(pos);
        if (south == null) return false;
        pos.set(x + 1, y, z);
        Chunk east = chunkStorage.get(pos);
        if (east == null) return false;
        pos.set(x - 1, y, z);
        Chunk west = chunkStorage.get(pos);
        return west != null;
    }

    public Hashtable<ChunkPos, Chunk> getChunkStorage() {
        return chunkStorage;
    }
}
