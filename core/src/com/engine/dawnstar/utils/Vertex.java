package com.engine.dawnstar.utils;

import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Vertex {
    public static final VertexAttributes ATTRIBUTES = new VertexAttributes(
            VertexAttribute.Position(),
            VertexAttribute.TexCoords(0),
            new VertexAttribute(VertexAttributes.Usage.Normal,1, ShaderProgram.NORMAL_ATTRIBUTE)
    );
}
