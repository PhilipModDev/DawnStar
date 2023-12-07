package com.dawnfall.engine.Server.world.gen;

import com.dawnfall.engine.Server.world.chunkUtil.ChunkPos;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkColumn;

public interface Generation {
    void generateColumn(ChunkPos offset);
    void generateColumn(ChunkColumn column);
}
