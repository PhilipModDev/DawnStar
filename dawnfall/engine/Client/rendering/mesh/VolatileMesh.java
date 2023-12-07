package com.dawnfall.engine.Client.rendering.mesh;

import com.badlogic.gdx.utils.Disposable;
import com.dawnfall.engine.Server.world.chunkUtil.Chunk;

public class VolatileMesh implements Disposable {
    public volatile ChunkMesh mesh;
    public volatile Chunk chunk;

    public VolatileMesh(ChunkMesh mesh, Chunk chunk){
        this.chunk = chunk;
        this.mesh = mesh;
    }

    @Override
    public void dispose() {
        if (mesh != null) mesh.dispose();
        if (chunk != null) {
            chunk.isNewChunk = true;
        }
    }
}
