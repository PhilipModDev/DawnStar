package com.engine.dawnstar.main.data;

import java.util.Objects;

public class ChunkPos {
    public int x;
    public int y;
    public int z;

    public ChunkPos(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(int x,int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public void set(ChunkPos chunkPos){
        this.x = chunkPos.x;
        this.y = chunkPos.y;
        this.z = chunkPos.z;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkPos chunkPos)) return false;
        return x == chunkPos.x && y == chunkPos.y && z == chunkPos.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
