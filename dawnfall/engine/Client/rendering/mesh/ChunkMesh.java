package com.dawnfall.engine.Client.rendering.mesh;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.dawnfall.engine.Server.util.IO.OptionProvider;
import com.dawnfall.engine.Server.util.glutils.VA;
import com.dawnfall.engine.Server.util.glutils.VBO;
import com.dawnfall.engine.Server.util.glutils.VertContext;
import com.dawnfall.engine.Server.util.glutils.Vertex;
import com.dawnfall.engine.Server.world.chunkUtil.Chunk;
import java.nio.ShortBuffer;

public class ChunkMesh implements Disposable {
    public boolean isDisposed;
    private final Chunk chunk;
    public Vertex vertex;
    private final int count;
    public ChunkMesh(Chunk chunk, FloatArray vertices, VertContext context) {
        this.chunk = chunk;
        if (OptionProvider.GL32){
            vertex = new VBO(vertices,context);
        }else {
            vertex = new VA(vertices, context);
        }
        count = vertices.size / Float.BYTES;
    }

    public void render(final IndexData indices) {
        vertex.bind();
        final ShortBuffer buffer = indices.getBuffer(false);
        buffer.limit(count);
        if (OptionProvider.GL32){
            Gdx.gl.glDrawElements(GL30.GL_TRIANGLES, count, GL30.GL_UNSIGNED_SHORT, buffer);
        }else {
            Gdx.gl.glDrawElements(GL20.GL_TRIANGLES, count, GL20.GL_UNSIGNED_SHORT, buffer);
        }
         vertex.unbind();
    }

    @Override
    public void dispose() {
        isDisposed = true;
        vertex.dispose();
    }
    @Override
    public boolean equals(Object obj) {
       if (!(obj instanceof ChunkMesh)) return false;
       ChunkMesh compareMesh = (ChunkMesh) obj;
       Chunk compareChunk = compareMesh.chunk;
       return chunk.x ==compareChunk.x&&chunk.y==compareChunk.y&&chunk.z== compareChunk.z;
    }
    public Chunk getChunk(){
        return this.chunk;
    }
}