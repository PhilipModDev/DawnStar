package com.engine.dawnstar.main.mesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.GridPoint3;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.engine.dawnstar.client.DawnStarClient;
import com.engine.dawnstar.main.ChunkBuilder;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.utils.ChunkBufferObject;
import com.engine.dawnstar.utils.math.Utils;

import java.nio.IntBuffer;
import java.util.Objects;

import static com.badlogic.gdx.Gdx.gl32;
import static com.engine.dawnstar.client.DawnStarClient.LOD_DISTANCE;

public class ChunkMesh implements Disposable {
    public Chunk chunk;
    public ChunkBufferObject chunkBufferObject;
    private final IntBuffer indexData;
    private final ShaderProgram voxelShader = DawnStarClient.getInstance().shaderUtils.getVoxelTerrain();
    private final Vector3 chunkPos = new Vector3();
    private final GridPoint3 chunkGridPos = new GridPoint3();

    public ChunkMesh(FloatArray array, IntBuffer indexData, Chunk chunk){
        this.chunk = chunk;
        this.indexData = indexData;
        chunkBufferObject = new ChunkBufferObject(array);
        chunkGridPos.set(chunk.localX,chunk.localY,chunk.localZ);
        chunkPos.set(chunk.localX * Chunk.SIZE,chunk.localY* Chunk.SIZE,chunk.localZ* Chunk.SIZE);
    }



    public void render(){
        //Sets the uniform variables for the chunk before rendering.
        voxelShader.setUniformf("chunkPos",chunkPos);
        chunkBufferObject.bind(indexData);

        if (Gdx.input.isKeyPressed(Input.Keys.L)){
            gl32.glDrawElements(GL32.GL_LINES,indexData.limit(),GL32.GL_UNSIGNED_INT,0);
        }else gl32.glDrawElements(GL32.GL_TRIANGLES,checkLOD(),GL32.GL_UNSIGNED_INT,0);

        chunkBufferObject.unbind();
    }

    private int checkLOD(){
        GridPoint3 playerPos = DawnStarClient.getInstance().getPlayerChunkPos();
        int distance = Utils.manhattanDistanceGrid(playerPos,chunkGridPos);
        if (distance > LOD_DISTANCE) return ChunkBuilder.LOD_3;
        if (distance > LOD_DISTANCE / 2) return ChunkBuilder.LOD_2;
        return ChunkBuilder.LOD_1;
    }

    @Override
    public void dispose() {
        indexData.clear();
        chunkBufferObject.dispose();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChunkMesh chunkMesh)) return false;
        return Objects.equals(chunk, chunkMesh.chunk);
    }
}
