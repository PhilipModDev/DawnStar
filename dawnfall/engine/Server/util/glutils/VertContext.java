package com.dawnfall.engine.Server.util.glutils;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public interface VertContext {
	 ShaderProgram getShader();
	 VertexAttributes getAttrs();
	 int getLocation(int i);
	/** @return vertexSize/Float.BYTE */
	 default int getAttrsSize() {
		return getAttrs().vertexSize/Float.BYTES;
	}
}
