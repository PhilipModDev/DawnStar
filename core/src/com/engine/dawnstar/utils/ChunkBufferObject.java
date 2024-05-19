package com.engine.dawnstar.utils;

import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.FloatArray;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static com.badlogic.gdx.Gdx.gl32;

public class ChunkBufferObject implements Disposable {
    //The single buffer data for the vbo.
    private final ByteBuffer byteBuffer;
    //The vao responsible for holding the attributes.
    private final int[] vao = new int[1];
    //Identifiers for ebo and vbo.
    private int vbo,ebo;
    //flag for is already uploaded.
    private boolean isUploadToGPU = false;
    //flag property for is already bind the attributes.
    private boolean isBind = false;

    public ChunkBufferObject(FloatArray array){
        //Creates a new byte buffer for performance with the size of the vertex multiplied by the array size divided by the attributes.
        byteBuffer = BufferUtils.newUnsafeByteBuffer(Vertex.ATTRIBUTES.vertexSize * (array.size/(Vertex.ATTRIBUTES.vertexSize/Float.BYTES)));
        //Copies the data into the buffer.
       BufferUtils.copy(array.items,byteBuffer,array.size,0);
    }

    //Only for uploading to the GPU.
    private void uploadToGPU(IntBuffer indexData){
        isUploadToGPU = true;
        gl32.glGenVertexArrays(vao.length,vao,vao[0]);
        gl32.glBindVertexArray(vao[0]);
        vbo = gl32.glGenBuffer();
        gl32.glBindBuffer(GL32.GL_ARRAY_BUFFER,vbo);
        gl32.glBufferData(GL32.GL_ARRAY_BUFFER,byteBuffer.limit() * Float.BYTES,byteBuffer,GL32.GL_STATIC_DRAW);
        ebo = gl32.glGenBuffer();
        gl32.glBindBuffer(GL32.GL_ELEMENT_ARRAY_BUFFER,ebo);
        gl32.glBufferData(GL32.GL_ELEMENT_ARRAY_BUFFER,indexData.limit() * Float.BYTES,indexData,GL32.GL_STATIC_DRAW);
    }

    //Binds the vbo for rendering and additional checks for uploads to GPU.
    public void bind(IntBuffer indexData) {
        if (!isUploadToGPU) uploadToGPU(indexData);

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
        gl32.glDeleteBuffer(vbo);
        gl32.glDeleteBuffer(ebo);
        gl32.glDeleteVertexArrays(vao.length, vao, 0);
        isUploadToGPU = false;
        isBind = false;
    }

}
