package com.dawnfall.engine.Server.world.chunkUtil;

public interface ChunkDataStorage {
     byte getBlockAt(int x, int y, int z);
     void setBlockAt(int x, int y, int z,byte blockId);
}
