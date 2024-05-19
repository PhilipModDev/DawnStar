package com.engine.dawnstar.main.mesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.engine.dawnstar.DawnStar;
import com.engine.dawnstar.main.data.Chunk;
import com.engine.dawnstar.utils.ChunkBufferObject;
import java.nio.IntBuffer;

import static com.badlogic.gdx.Gdx.gl32;

public class ChunkMesh implements Disposable {
    public Chunk chunk;
    public ChunkBufferObject chunkBufferObject;
    private final IntBuffer indexData;
    private final ShaderProgram voxelShader = DawnStar.getInstance().shaderUtils.getVoxelTerrain();
    private final Vector3 chunkPos = new Vector3();

    public ChunkMesh(FloatArray array, IntBuffer indexData, Chunk chunk){
        this.chunk = chunk;
        this.indexData = indexData;
        chunkBufferObject = new ChunkBufferObject(array);
        chunkPos.set(chunk.localX * Chunk.SIZE,chunk.localY* Chunk.SIZE,chunk.localZ* Chunk.SIZE);
    }



    public void render(){
        //Sets the uniform variables for the chunk before rendering.

        voxelShader.setUniformf("chunkPos",chunkPos);
        chunkBufferObject.bind(indexData);

        gl32.glEnable(GL32.GL_DEPTH_TEST);
        gl32.glDepthFunc(GL32.GL_LEQUAL);
        gl32.glEnable(GL32.GL_CULL_FACE);

        if (Gdx.input.isKeyPressed(Input.Keys.L)){
         gl32.glDrawElements(GL32.GL_LINES,indexData.limit(),GL32.GL_UNSIGNED_INT,0);
        }else gl32.glDrawElements(GL32.GL_TRIANGLES,indexData.limit(),GL32.GL_UNSIGNED_INT,0);

        chunkBufferObject.unbind();
    }

    @Override
    public void dispose() {
        indexData.clear();
        chunkBufferObject.dispose();
    }
}
