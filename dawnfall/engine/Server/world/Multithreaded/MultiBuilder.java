package com.dawnfall.engine.Server.world.Multithreaded;

import com.dawnfall.engine.Client.rendering.mesh.VolatileMesh;
import com.dawnfall.engine.Server.util.Constants;
import com.dawnfall.engine.Server.util.Utils;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkManager;
import com.dawnfall.engine.Server.world.chunkUtil.ChunkPos;
import java.util.ArrayDeque;
import java.util.Queue;

public class MultiBuilder implements Runnable {
    private final Queue<ChunkPos> CHUNK_POS_QUEUE;
    private final ChunkManager chunkManager;
    private boolean done = true;
    public MultiBuilder(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
        CHUNK_POS_QUEUE = new ArrayDeque<>();
    }
    public void queMeshBuilding(Queue<ChunkPos> queue){
        synchronized (queue) {
            while (!queue.isEmpty()) {
                if (CHUNK_POS_QUEUE.size() >= Constants.CHUNKS_FRAME_BASE) break;
                ChunkPos chunkPos = queue.poll();
                if (chunkPos == null) continue;
                CHUNK_POS_QUEUE.add(chunkPos);
            }
            if (done) startWorkLoad();
        }
    }
    private void startWorkLoad(){
        chunkManager.threadUtilsFactory.execute(this);
    }
    public boolean isDone(){
      return done;
    }

    @Override
    public void run() {
        try {
         done = false;
            while (!CHUNK_POS_QUEUE.isEmpty()) {
                ChunkPos chunkPos = CHUNK_POS_QUEUE.poll();
                if (chunkPos == null) continue;
                VolatileMesh volatileMesh = chunkManager.createChunkAt(chunkPos);
                if (volatileMesh == null) continue;
                if (volatileMesh.mesh == null) continue;
                chunkManager.threadUtilsFactory.addValueToQueue(chunkManager.CHUNK_UPDATES, volatileMesh);
            }
            done = true;
        }catch (Exception exception){
            Utils.log("Worker Thread", "Overloading.");
            exception.printStackTrace();
            done = true;
            chunkManager.threadUtilsFactory.execute(this);
        }
    }
}

