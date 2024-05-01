package com.engine.dawnstar.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.glutils.IndexData;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import com.engine.dawnstar.DawnStar;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import static com.badlogic.gdx.Gdx.gl32;

public class VertexBufferObject implements Disposable {
    //The single buffer data for the vbo.
    private final ByteBuffer byteBuffer;
    //Main shader for the voxels.
    private final ShaderProgram shader = DawnStar.getInstance().shaderProgram;
    //The vao responsible for holding the attributes.
    private final int[] vao = new int[1];
    //Identifiers for ebo and vbo.
    private int vbo,ebo;
    //flag for is already uploaded.
    private boolean isUploadToGPU = false;
    //flag property for is already bind the attributes.
    private boolean isBind = false;

    public VertexBufferObject(FloatArray array){
        //Creates a new byte buffer for performance with the size of the vertex multiplied by the array size divided by the attributes.
        byteBuffer = BufferUtils.newUnsafeByteBuffer(Vertex.ATTRIBUTES.vertexSize * (array.size/Vertex.ATTRIBUTES.size()));
        //Copies the data into the buffer.
       BufferUtils.copy(array.items,byteBuffer,array.size,0);
    }

    //Only for uploading to the GPU.
    private void uploadToGPU(IndexData indexData){
        isUploadToGPU = true;
        gl32.glGenVertexArrays(vao.length,vao,vao[0]);
        gl32.glBindVertexArray(vao[0]);
        vbo = gl32.glGenBuffer();
        gl32.glBindBuffer(GL32.GL_ARRAY_BUFFER,vbo);
        gl32.glBufferData(GL32.GL_ARRAY_BUFFER,byteBuffer.limit() * Float.BYTES,byteBuffer,GL32.GL_STATIC_DRAW);
        ebo = gl32.glGenBuffer();
        gl32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER,ebo);
        ShortBuffer indicesBuffer = indexData.getBuffer(false);
        gl32.glBufferData(GL32.GL_ELEMENT_ARRAY_BUFFER,indicesBuffer.limit() * Float.BYTES,indicesBuffer,GL32.GL_STATIC_DRAW);
    }

    //Binds the vbo for rendering and additional checks for uploads to GPU.
    public void bind(Camera camera, IndexData indexData) {
        if (!isUploadToGPU) uploadToGPU(indexData);

        shader.bind();
        shader.setUniformMatrix("combined",camera.combined);

        gl32.glBindVertexArray(vao[0]);
        gl32.glBindBuffer(GL32.GL_ARRAY_BUFFER,vbo);

        if (!isBind){
            for (int i = 0; i < Vertex.ATTRIBUTES.size(); i++) {
                VertexAttribute attribute = Vertex.ATTRIBUTES.get(i);
                gl32.glVertexAttribPointer(i,attribute.numComponents,attribute.type,attribute.normalized,Vertex.ATTRIBUTES.vertexSize,attribute.offset);
                gl32.glEnableVertexAttribArray(i);
            }
            isBind = true;
        }
    }

    //Unbinds the vbo to conserve memory.
    public void unbind(){
        gl32.glBindBuffer(GL32.GL_ARRAY_BUFFER,0);
        gl32.glBindVertexArray(0);
    }

    //Deletes the vbo to free up memory and more performance.
    @Override
    public void dispose() {
        BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
        isUploadToGPU = false;
        isBind = false;
        gl32.glDeleteBuffer(vbo);
        gl32.glDeleteBuffer(ebo);
        gl32.glBindVertexArray(0);
    }
}
