package com.dawnfall.engine.Server.util.glutils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.FloatArray;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public final class VBO implements Vertex {
	private final VertContext context;
	private final FloatBuffer buffer;
	private final ByteBuffer byteBuffer;
	private int vboId;
	private final int[] vao = new int[1];
	private boolean isUploaded;
	private final GL30 gl30 = Gdx.gl30;
	public VBO(FloatArray array, final VertContext context) {
		this.context = context;
		byteBuffer = BufferUtils.newUnsafeByteBuffer(context.getAttrs().vertexSize * (array.size/context.getAttrsSize()));
		buffer = byteBuffer.asFloatBuffer();
		buffer.flip();
		byteBuffer.flip();
		BufferUtils.copy(array.items, byteBuffer, array.size, 0);
		buffer.position(0);
		buffer.limit(array.size);
	}

	@Override
	public void bind() {
		if (!isUploaded) uploadToGPU();
		gl30.glBindBuffer(GL30.GL_ARRAY_BUFFER,vboId);
        gl30.glBindVertexArray(vao[0]);
		byteBuffer.limit(buffer.limit() * Float.BYTES);
		final VertexAttributes attributes = context.getAttrs();
		final ShaderProgram shader = context.getShader();
		final int numAttributes = context.getAttrs().size();//Number of attributes
		for (int i = 0; i < numAttributes; i++) {
			//Loops 3 times.
			final VertexAttribute attribute = attributes.get(i);
			//Gets the shader's attributes variables, as well getting list of attributes.
			final int location = context.getLocation(i);
			shader.enableVertexAttribute(location);
			shader.setVertexAttribute(location, attribute.numComponents, attribute.type, attribute.normalized, attributes.vertexSize, attribute.offset);
		}
	}

	@Override
	public void unbind() {
		final VertexAttributes attributes = context.getAttrs();
		final ShaderProgram shader = context.getShader();
		final int numAttributes = attributes.size();
		for (int i = 0; i < numAttributes; i++) {
			final int location = context.getLocation(i);
			if (location >= 0) shader.disableVertexAttribute(location);
		}
		gl30.glBindBuffer(GL30.GL_ARRAY_BUFFER,0);
	}
	private void uploadToGPU(){
		isUploaded = true;
		gl30.glGenVertexArrays(vao.length,vao,0);
		vboId = gl30.glGenBuffer();
		gl30.glBindBuffer(GL30.GL_ARRAY_BUFFER,vboId);
		gl30.glBufferData(GL30.GL_ARRAY_BUFFER,buffer.limit() * Float.BYTES,buffer,GL30.GL_STATIC_DRAW);
	}

	@Override
	public void dispose() {
		if (isUploaded){
			gl30.glDeleteBuffer(vboId);
			gl30.glDeleteVertexArrays(vao.length,vao,0);
		}
		BufferUtils.disposeUnsafeByteBuffer(byteBuffer);
	}
}
