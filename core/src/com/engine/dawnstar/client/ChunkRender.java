package com.engine.dawnstar.client;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.engine.dawnstar.main.ChunkBuilder;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.main.data.ChunkManager;
import com.engine.dawnstar.main.mesh.ChunkMesh;
import com.engine.dawnstar.utils.math.FrustumUtil;

import java.util.Optional;

import static com.badlogic.gdx.Gdx.gl32;

public final class ChunkRender implements Disposable {
    private final ChunkBuilder chunkBuilder;
    private final Array<ChunkMesh> chunkBuffer_0;
    private final Array<ChunkMesh> chunkBuffer_1;
    private Array<ChunkMesh> chunks;
    private final ChunkManager chunkManager;
    private final DawnStarClient client;

    public ChunkRender(DawnStarClient client){
        this.client = client;
        chunkManager = new ChunkManager();
        chunkBuilder = new ChunkBuilder(chunkManager);
        chunkBuffer_0 = new Array<>();
        chunkBuffer_1 = new Array<>();
        chunks = new Array<>();
    }

    public void addChunkMesh(Chunk chunk){
        Array<ChunkMesh> currentBuffer = getCurrentBuffer();
        Optional<ChunkMesh> optionalChunkMesh = chunkBuilder.create(chunk);
        if (optionalChunkMesh.isEmpty()) return;
        ChunkMesh chunkMesh = optionalChunkMesh.get();
        currentBuffer.add(chunkMesh);
    }

    public void checkMeshChunks() {
        Array<ChunkMesh> currentBuffer = getCurrentBuffer();
        chunkManager.getChunkStorage().forEach((key,value) -> {
            if (value.isVisible) return;
            if (chunkManager.sixChunkCheck(value.localX, value.localY, value.localZ)){
                value.isVisible = true;
                Optional<ChunkMesh> optionalChunkMesh = chunkBuilder.create(value);
                if (optionalChunkMesh.isEmpty()) return;
                ChunkMesh chunkMesh = optionalChunkMesh.get();
                if (!currentBuffer.contains(chunkMesh,false)) {
                    currentBuffer.add(chunkMesh);
                }
            }
        });
    }

    public void buildChunk(Chunk chunk) {
        chunkManager.addToStorage(chunk);
    }

    public void render(){

        swapBuffers();

        Array<ChunkMesh> buffer = getCurrentBuffer();
        //Binds for voxel rendering.
        client.gameAsset.getAtlasTexture().bind();
        client.shaderUtils.bindTerrain();
        gl32.glEnable(GL32.GL_DEPTH_TEST);
        gl32.glDepthFunc(GL32.GL_LEQUAL);
        gl32.glEnable(GL32.GL_CULL_FACE);

        for (int i = 0; i < buffer.size; i++) {
            ChunkMesh chunkMesh = buffer.get(i);
            if (!isChunkVisible(chunkMesh.chunk)) continue;
            chunkMesh.render();
        }

        //Unbinds for voxel rendering.
        gl32.glDisable(GL32.GL_DEPTH_TEST);
        gl32.glDisable(GL32.GL_CULL_FACE);
    }

    private void swapBuffers(){
//        chunks.clear();
//       if (chunks == chunkBuffer_0){
//           chunks = chunkBuffer_1;
//       } else chunks = chunkBuffer_0;
    }

    public Array<ChunkMesh> getCurrentBuffer(){
       return chunks;
    }

    private boolean isChunkVisible(Chunk chunk) {
        Camera camera = client.cameraUtils.getCamera3D();
        return FrustumUtil.frustumBounds(camera.frustum.planes,chunk);
    }
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    @Override
    public void dispose() {
        chunks.forEach(ChunkMesh::dispose);
        chunkManager.getChunkStorage().clear();
    }
}
