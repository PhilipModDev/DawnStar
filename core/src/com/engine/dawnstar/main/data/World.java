package com.engine.dawnstar.main.data;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.engine.dawnstar.client.DawnStarClient.RENDER_DISTANCE;

public final class World {

    public final Hashtable<ChunkPos,Chunk> chunks = new Hashtable<>();
    public static final int WORD_SIZE = 1023;

    private boolean isDoneLoading;

    public World()  {
        for (int x = 0; x < RENDER_DISTANCE; x++) {
            for (int y = 0; y < RENDER_DISTANCE; y++) {
                for (int z = 0; z < RENDER_DISTANCE; z++) {
                    Chunk chunk = new Chunk(x ,y ,z);
                    generateChunk(chunk);
                    ChunkPos chunkPos = new ChunkPos(x ,y,z);
                    chunks.put(chunkPos,chunk);
                }
            }
        }
        isDoneLoading = true;
    }


    public List<Chunk> fetchChunksInView(int centerX,int centerY,int centerZ,int view){
        List<Chunk> fetchChunks = new ArrayList<>();

        for (int x = centerX; x <=  view; x++) {
            for (int y = centerY ; y <=  view; y++) {
                for (int z = centerZ ; z <=  view; z++) {
                    ChunkPos chunkPos = new ChunkPos(x,y,z);
                    Chunk currentChunk = chunks.get(chunkPos);

                    if (currentChunk != null) fetchChunks.add(currentChunk);
                }
            }
        }
        return fetchChunks;
    }

    private void generateChunk(Chunk chunk){
        ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
        for (int xi = 0; xi < Chunk.SIZE; xi++) {
            for (int yi = 0; yi < Chunk.SIZE; yi++) {
                for (int zi = 0; zi < Chunk.SIZE; zi++) {
//                    if (threadLocalRandom.nextInt(100) > 98){
//                        chunk.setBlock(xi, yi, zi, (byte) 0);
//                    } else chunk.setBlock(xi, yi, zi, (byte) 1);
                   chunk.setBlock(xi, yi, zi, (byte) 1);
                }
            }
        }

        chunk.setBlock(31,2,0, (byte) 0);
    }

    public Chunk getChunkAt(ChunkPos chunkPos){
        return chunks.get(chunkPos);
    }

    public Chunk getChunkAt(int x,int y, int z){
        ChunkPos chunkPos = new ChunkPos(x,y,z);
        return chunks.get(chunkPos);
    }

    public boolean isDoneLoading() {
        return isDoneLoading;
    }
}
